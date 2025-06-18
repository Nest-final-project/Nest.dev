package caffeine.nest_dev.domain.payment.service;

import caffeine.nest_dev.common.enums.ErrorCode;
import caffeine.nest_dev.common.exception.BaseException;
import caffeine.nest_dev.domain.payment.dto.request.PaymentCancelRequestDto;
import caffeine.nest_dev.domain.payment.dto.request.PaymentConfirmRequestDto;
import caffeine.nest_dev.domain.payment.dto.request.PaymentPrepareRequestDto;
import caffeine.nest_dev.domain.payment.dto.response.PaymentConfirmResponseDto;
import caffeine.nest_dev.domain.payment.dto.response.PaymentDetailsResponseDto;
import caffeine.nest_dev.domain.payment.dto.response.PaymentPrepareResponseDto;
import caffeine.nest_dev.domain.payment.dto.response.TossApproveResponse;
import caffeine.nest_dev.domain.payment.dto.response.TossCancelResponse;
import caffeine.nest_dev.domain.payment.dto.response.TossPaymentInquiryResponse;
import caffeine.nest_dev.domain.payment.entity.Payment;
import caffeine.nest_dev.domain.payment.enums.PaymentStatus;
import caffeine.nest_dev.domain.payment.enums.PaymentType;
import caffeine.nest_dev.domain.payment.repository.PaymentRepository;
import caffeine.nest_dev.domain.reservation.entity.Reservation;
import caffeine.nest_dev.domain.reservation.repository.ReservationRepository;
import caffeine.nest_dev.domain.ticket.entity.Ticket;
import caffeine.nest_dev.domain.ticket.repository.TicketRepository;
import caffeine.nest_dev.domain.user.entity.User;
import caffeine.nest_dev.domain.user.repository.UserRepository;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    // 결제, 예약, 유저 , 티켓 repositroy 의존성 주입
    private final PaymentRepository paymentRepository; // 결제 데이터 저장 / 조회
    private final ReservationRepository reservationRepository; // 예약 데이터 조회
    private final UserRepository userRepository; // 유저 데이터 조회
    private final TicketRepository ticketRepository; // 티켓 데이터 조회
    private final WebClient webClient; // 외부 API(Toss) 호출용 / 기존 RestTemplate 대신 WebClient 주입

    @Value("${toss.payments.client-secret}")
    private String tossSecretKey; //application.properties에 저장된 Toss Secret Key 주입

    // 결제 준비 (결제 대기 상태 생성)
    @Transactional
    public PaymentPrepareResponseDto preparePayment(PaymentPrepareRequestDto requestDto,
            String userEmail) {
        // 이메일 유저 조회, 탈퇴한 유저 제외
        User user = userRepository.findByEmailAndIsDeletedFalse(userEmail)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        // 예약 PK 파싱 (String -> Long)
        Long reservationPk = Long.parseLong(requestDto.getReservationId());

        // 예약 조회 시 예외 발생
        Reservation reservation = reservationRepository.findById(reservationPk)
                .orElseThrow(() -> new BaseException(ErrorCode.RESERVATION_NOT_FOUND));

        // 티켓 조회 시 예외 발생
        Ticket ticket = ticketRepository.findById(requestDto.getTicketId())
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_TICKET));

        // 예약 유저 확인
        if (!reservation.getMentee().equals(user)) {
            throw new BaseException(ErrorCode.NO_PAYMENT_AUTHORITY);
        }

        // 결제 DB 생성 및 저장
        Payment payment = Payment.builder().reservation(reservation).amount(requestDto.getAmount())
                .status(PaymentStatus.READY) // 결제 대기 상태로 생성
                .payer(user).ticket(ticket).paymentType(PaymentType.TOSSPAY) // 토스 결제 한정?
                .build();
        paymentRepository.save(payment);

        // 예약ID, 티켓명 반환 (프론트 결제창 오픈 등에 사용)
        return new PaymentPrepareResponseDto(String.valueOf(reservation.getId()), ticket.getName());
    }

    // 결제 승인 (토스 결제 완료 처리)
    @Transactional
    public PaymentConfirmResponseDto confirmPayment(PaymentConfirmRequestDto requestDto,
            String userEmail, Long reservationId) {
        // 결제 정보 DB 조회
        Payment payment = paymentRepository.findByReservationId((reservationId))
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_ORDER));

        // 결제자 확인
        if (!payment.getPayer().getEmail().equals(userEmail)) {
            throw new BaseException(ErrorCode.NO_PAYMENT_INFO_AUTHORITY);
        }

        // 결제 금액 확인
        if (!payment.getAmount().equals(requestDto.getAmount())) {
            throw new BaseException(ErrorCode.INVALID_PAYMENT_AMOUNT);
        }

        // 결제 유무 확인
        if (payment.getStatus() != PaymentStatus.READY) {
            throw new BaseException(ErrorCode.ALREADY_PROCESSED_OR_CANCELED);
        }

        // 토스 결제 승인 API 호출
        TossApproveResponse tossResponse = requestTossPaymentApproval(requestDto).block();

        // 결제 승인 -> DB 업데이트
        if (tossResponse != null && "DONE".equals(tossResponse.getStatus())) {
            log.info("[결제 승인 성공] orderId: {}", tossResponse.getOrderId());
            payment.updateOnSuccess(tossResponse.getPaymentKey(), tossResponse.getMethod(),
                    tossResponse.getApprovedAt(), tossResponse.getRequestedAt());

            // 응답 DTO 변환 후 반환
            return PaymentConfirmResponseDto.of(payment);
        } else {
            payment.updateOnFailure();
            throw new BaseException(ErrorCode.PAYMENT_APPROVE_FAILED);
        }
    }

    // 결제 상세 정보 조회 (DB + 토스 실시간 상태)
    @Transactional
    public PaymentDetailsResponseDto getPaymentDetails(Long paymentId, String userEmail) {
        // 결제 데이터 조회
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_ORDER));

        // 본인 결제 내역 확인
        if (!payment.getPayer().getEmail().equals(userEmail)) {
            throw new BaseException(ErrorCode.NO_PAYMENT_VIEW_AUTHORITY);
        }

        // 결제키가 없는 경우 -> DB 데이터만 반환
        if (payment.getPaymentKey() == null) {
            return PaymentDetailsResponseDto.from(payment);
        }

        // 토스 결제 상세 API 실시간 상태 / 정보 받아오기
        TossPaymentInquiryResponse tossResponse = requestTossPaymentInquiry(
                payment.getPaymentKey()).block();

        // 토스 API 응답 있을 시 실시간 정보 응답 구성
        if (tossResponse != null) {
            return PaymentDetailsResponseDto.builder()
                    .reservationId(String.valueOf(payment.getReservation().getId()))
                    .ticketName(payment.getTicket().getName())
                    .payerName(payment.getPayer().getName())
                    .paymentKey(tossResponse.getPaymentKey())
                    .paymentStatus(tossResponse.getStatus()) // DB 상태 대신 토스의 실시간 상태를 사용
                    .paymentMethod(tossResponse.getMethod()) // DB 정보 대신 토스의 실시간 정보를 사용
                    .amount(tossResponse.getTotalAmount()).approvedAt(tossResponse.getApprovedAt())
                    .build();
        }
        // 아닐 시 DB 정보 반환
        return PaymentDetailsResponseDto.from(payment);
    }

    // 결제 취소 (환불)
    @Transactional
    public void cancelPayment(Long paymentId, PaymentCancelRequestDto cancelDto, String userEmail) {
        // 결제 내역 조회
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BaseException(ErrorCode.PAYMENT_NOT_FOUND));

        // 본인 결제 확인
        if (!payment.getPayer().getEmail().equals(userEmail)) {
            throw new BaseException(ErrorCode.NO_PAYMENT_CANCEL_AUTHORITY);
        }

        // 결제 완료 상태 시 취소 허용
        if (payment.getStatus() != PaymentStatus.PAID) {
            throw new BaseException(ErrorCode.ONLY_PAID_CAN_BE_CANCELED);
        }

        // 토스 결제 취소 API 호출 (실제 환불)
        TossCancelResponse tossResponse = requestTossPaymentCancel(payment.getPaymentKey(),
                cancelDto.getCancelReason()).block();

        // 취소 성공 시 DB 갱신 / 실패 시 예외 발생
        if (tossResponse != null && "CANCELED".equals(tossResponse.getStatus())) {
            payment.updateOnCancel(cancelDto.getCancelReason());
            log.info("결제가 성공적으로 취소되었습니다. paymentKey: {}", payment.getPaymentKey());
        } else {
            log.error("토스 결제 취소에 실패했습니다. paymentKey: {}", payment.getPaymentKey());
            throw new BaseException(ErrorCode.PAYMENT_CANCEL_FAILED);
        }
    }

    // [토스 결제 승인] API 호출
    private Mono<TossApproveResponse> requestTossPaymentApproval(
            PaymentConfirmRequestDto requestDto) {
        String url = "https://api.tosspayments.com/v1/payments/confirm"; // 승인 API URL
        // 토스 API 인증: 시크릿키 base64 인코딩
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode(
                (tossSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        String authorizations = "Basic " + new String(encodedBytes);

        // WebClient로 토스 API에 POST 요청, 인증 헤더 추가
        return webClient.post().uri(url).header(HttpHeaders.AUTHORIZATION, authorizations)
                .contentType(MediaType.APPLICATION_JSON).bodyValue(requestDto) // 요청 DTO 바디 전송
                .retrieve().onStatus(HttpStatusCode::isError,
                        response -> response.bodyToMono(String.class).flatMap(errorBody -> {
                            log.info("[결제 승인 요청] paymentKey={}, orderId={}, amount={}",
                                    requestDto.getPaymentKey(), requestDto.getOrderId(),
                                    requestDto.getAmount());
                            log.info("[Toss 요청 전송] requestDto = {}", requestDto);
                            log.error("Toss API 호출 실패: status={}, body={}", response.statusCode(),
                                    errorBody);
                            return Mono.error(new BaseException(ErrorCode.TOSS_API_FAILED));
                        })).bodyToMono(TossApproveResponse.class);
    }

    // [토스 결제 조회] API 호출
    private Mono<TossPaymentInquiryResponse> requestTossPaymentInquiry(String paymentKey) {
        String url = "https://api.tosspayments.com/v1/payments/" + paymentKey; // 결제 단건 조회 URL
        String authorizations = "Basic " + Base64.getEncoder()
                .encodeToString((tossSecretKey + ":").getBytes(StandardCharsets.UTF_8)); // 인증 헤더

        // WebClient GET 요청
        return webClient.get() // POST -> GET
                .uri(url).header(HttpHeaders.AUTHORIZATION, authorizations).retrieve()
                .onStatus(HttpStatusCode::isError,
                        response -> response.bodyToMono(String.class).flatMap(errorBody -> {
                            log.error("Toss Inquiry API Error - Status: {}, Body: {}",
                                    response.statusCode(), errorBody);
                            return Mono.error(new BaseException(ErrorCode.TOSS_API_FAILED));
                        })).bodyToMono(TossPaymentInquiryResponse.class); // 응답 DTO 변환
    }

    // [토스 결제 취소] API 호출
    private Mono<TossCancelResponse> requestTossPaymentCancel(String paymentKey,
            String cancelReason) {
        String url =
                "https://api.tosspayments.com/v1/payments/" + paymentKey + "/cancel"; // 결제 취소 URL
        String authorizations = "Basic " + Base64.getEncoder()
                .encodeToString((tossSecretKey + ":").getBytes(StandardCharsets.UTF_8)); // 인증 헤더

        String idempotencyKey = UUID.randomUUID().toString(); // Idempotency 랜덤키

        // WebClient POST 요청
        return webClient.post().uri(url).header(HttpHeaders.AUTHORIZATION, authorizations)
                .header("Idempotency-Key", idempotencyKey) // 멱등성 보장 헤더
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("cancelReason", cancelReason)) // 취소 사유 전송
                .retrieve().onStatus(HttpStatusCode::isError,
                        response -> response.bodyToMono(String.class).flatMap(errorBody -> {
                            log.error("Toss Cancel API Error - Status: {}, Body: {}",
                                    response.statusCode(), errorBody);
                            return Mono.error(new BaseException(ErrorCode.TOSS_CANCEL_API_FAILED));
                        })).bodyToMono(TossCancelResponse.class); // 응답 DTO 변환
    }
}
