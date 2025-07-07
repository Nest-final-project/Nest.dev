package caffeine.nest_dev.domain.payment.service;

import caffeine.nest_dev.common.dto.PagingResponse;
import caffeine.nest_dev.common.enums.ErrorCode;
import caffeine.nest_dev.common.exception.BaseException;
import caffeine.nest_dev.domain.chatroom.scheduler.event.SaveCreateRoomEvent;
import caffeine.nest_dev.domain.coupon.entity.UserCoupon;
import caffeine.nest_dev.domain.coupon.entity.UserCouponId;
import caffeine.nest_dev.domain.coupon.repository.UserCouponRepository;
import caffeine.nest_dev.domain.coupon.service.UserCouponService;
import caffeine.nest_dev.domain.payment.dto.request.PaymentCancelRequestDto;
import caffeine.nest_dev.domain.payment.dto.request.PaymentConfirmRequestDto;
import caffeine.nest_dev.domain.payment.dto.request.PaymentPrepareRequestDto;
import caffeine.nest_dev.domain.payment.dto.response.PaymentConfirmResponseDto;
import caffeine.nest_dev.domain.payment.dto.response.PaymentDetailsResponseDto;
import caffeine.nest_dev.domain.payment.dto.response.PaymentPrepareResponseDto;
import caffeine.nest_dev.domain.payment.dto.response.PaymentsResponseDto;
import caffeine.nest_dev.domain.payment.dto.response.TossApproveResponse;
import caffeine.nest_dev.domain.payment.dto.response.TossCancelResponse;
import caffeine.nest_dev.domain.payment.dto.response.TossPaymentInquiryResponse;
import caffeine.nest_dev.domain.payment.entity.Payment;
import caffeine.nest_dev.domain.payment.enums.PaymentStatus;
import caffeine.nest_dev.domain.payment.enums.PaymentType;
import caffeine.nest_dev.domain.payment.repository.PaymentRepository;
import caffeine.nest_dev.domain.reservation.entity.Reservation;
import caffeine.nest_dev.domain.reservation.enums.ReservationStatus;
import caffeine.nest_dev.domain.reservation.repository.ReservationRepository;
import caffeine.nest_dev.domain.ticket.entity.Ticket;
import caffeine.nest_dev.domain.ticket.repository.TicketRepository;
import caffeine.nest_dev.domain.user.entity.User;
import caffeine.nest_dev.domain.user.entity.UserDetailsImpl;
import caffeine.nest_dev.domain.user.repository.UserRepository;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final UserCouponRepository userCouponRepository;
    private final UserCouponService userCouponService;
    private final WebClient webClient; // 외부 API(Toss) 호출용 / 기존 RestTemplate 대신 WebClient 주입

    @Value("${toss.payments.client-secret}")
    private String tossSecretKey; //application.properties에 저장된 Toss Secret Key 주입

    // 이벤트 등록
    private final ApplicationEventPublisher eventPublisher;

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

        // 기존 결제 확인 - 중복 결제 방지
        Optional<Payment> existingPayment = paymentRepository.findByReservationId(reservationPk);
        if (existingPayment.isPresent()) {
            Payment payment = existingPayment.get();

            // 이미 결제 완료된 경우
            if (payment.getStatus() == PaymentStatus.PAID) {
                log.warn("이미 결제 완료된 예약입니다: reservationId={}, paymentId={}",
                        reservationPk, payment.getId());
                throw new BaseException(ErrorCode.ALREADY_PAID);
            }

            // 기존 대기 중인 결제가 있는 경우 - 기존 결제 정보 반환
            if (payment.getStatus() == PaymentStatus.READY) {
                log.info("기존 대기 중인 결제 발견. 기존 결제 정보 반환: reservationId={}, paymentId={}",
                        reservationPk, payment.getId());
                return new PaymentPrepareResponseDto(String.valueOf(reservation.getId()),
                        ticket.getName());
            }

            // 실패하거나 취소된 결제가 있는 경우 - 기존 결제 삭제 후 새로 생성
            if (payment.getStatus() == PaymentStatus.FAILED
                    || payment.getStatus() == PaymentStatus.CANCELED) {
                log.info(
                        "실패/취소된 결제 발견. 기존 결제 삭제 후 새로 생성: reservationId={}, paymentId={}, status={}",
                        reservationPk, payment.getId(), payment.getStatus());
                paymentRepository.delete(payment);
            }
        }

        int originalAmount = requestDto.getAmount();
        UserCoupon userCoupon = null;

        if (requestDto.getCouponId() != null) {
            UserCouponId userCouponId = new UserCouponId(user.getId(), requestDto.getCouponId());
            userCoupon = userCouponRepository.findById(userCouponId)
                    .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_USER_COUPON));
            if (userCoupon.isUsed()) {
                log.warn("중복 사용 쿠폰: couponId={}, userId={}", userCoupon.getCoupon().getId(),
                        userCoupon.getUser().getId());
                throw new BaseException(ErrorCode.COUPON_ALREADY_USED);
            }

            // 새로운 쿠폰 검증 및 할인 계산 로직 사용
            BigDecimal orderAmount = BigDecimal.valueOf(originalAmount);
            userCouponService.validateCouponForUse(userCoupon.getCoupon(), orderAmount);

            BigDecimal calculatedDiscount = userCouponService.calculateDiscount(userCoupon.getCoupon(), orderAmount);
            if (calculatedDiscount.compareTo(orderAmount) > 0) {
                throw new BaseException(ErrorCode.INVALID_DISCOUNT_AMOUNT);
            }
        }

        // DB에는 원가 저장, 쿠폰 할인은 결제 승인 시 처리
        Payment payment = Payment.builder()
                .reservation(reservation)
                .amount(originalAmount)          // 원가 저장 (토스 결제 기준)
                .originalAmount(originalAmount)  // 원가 별도 저장
                .discountAmount(0)               // 아직 할인 적용 안함
                .status(PaymentStatus.READY)     // 결제 대기 상태로 생성
                .payer(user)
                .ticket(ticket)
                .paymentType(PaymentType.TOSSPAY)
                .userCoupon(userCoupon)          // 쿠폰 정보만 연결
                .build();
        Payment savedPayment = paymentRepository.save(payment);

        log.info("새 결제 생성 완료: reservationId={}, paymentId={}, originalAmount={}, coupon={}",
                reservationPk, savedPayment.getId(), originalAmount,
                userCoupon != null ? "적용" : "없음");

        // 예약ID, 티켓명 반환 (프론트 결제창 오픈 등에 사용)
        return new PaymentPrepareResponseDto(String.valueOf(reservation.getId()), ticket.getName());
    }

    // 결제 승인 (토스 결제 완료 처리)
    @Transactional
    public PaymentConfirmResponseDto confirmPayment(PaymentConfirmRequestDto requestDto,
            String userEmail, Long reservationId) {

        log.info(" 결제 승인 요청 시작 - reservationId: {}, userEmail: {}", reservationId, userEmail);
        log.info(" 결제 승인 요청 데이터: {}", requestDto);

        // 결제 정보 DB 조회
        Payment payment = paymentRepository.findByReservationId(reservationId)
                .orElseThrow(() -> {
                    log.error(" 예약 ID {}에 대한 결제 정보를 찾을 수 없습니다", reservationId);

                    // 디버깅을 위해 해당 예약이 존재하는지 확인
                    Optional<Reservation> reservation = reservationRepository.findById(
                            reservationId);
                    if (reservation.isPresent()) {
                        log.warn(" 예약은 존재하지만 결제 정보가 없습니다. 예약 ID: {}, 예약 상태: {}",
                                reservationId, reservation.get().getReservationStatus());
                    } else {
                        log.error(" 예약 자체가 존재하지 않습니다. 예약 ID: {}", reservationId);
                    }

                    return new BaseException(ErrorCode.NOT_FOUND_ORDER);
                });

        log.info(" 결제 정보 조회 성공 - paymentId: {}, status: {}", payment.getId(), payment.getStatus());

        // 결제자 확인
        if (!payment.getPayer().getEmail().equals(userEmail)) {
            throw new BaseException(ErrorCode.NO_PAYMENT_INFO_AUTHORITY);
        }

        //  원가 기준으로 토스 결제 금액 검증
        if (!payment.getOriginalAmount().equals(requestDto.getAmount())) {
            log.error(" 결제 금액 불일치 - DB 원가: {}, 토스 결제: {}",
                    payment.getOriginalAmount(), requestDto.getAmount());
            throw new BaseException(ErrorCode.INVALID_PAYMENT_AMOUNT);
        }

        // 결제 유무 확인
        if (payment.getStatus() != PaymentStatus.READY) {
            log.warn(" [중복 승인 시도] 이미 처리된 결제: paymentId={}, status={}, paymentKey={}",
                    payment.getId(), payment.getStatus(), payment.getPaymentKey());
            throw new BaseException(ErrorCode.ALREADY_PROCESSED_OR_CANCELED);
        }

        // 이미 paymentKey가 있는 경우 중복 승인 시도 방지
        if (payment.getPaymentKey() != null && !payment.getPaymentKey().isEmpty()) {
            log.warn(" [이미 승인된 결제] paymentId={}, paymentKey={}",
                    payment.getId(), payment.getPaymentKey());
            throw new BaseException(ErrorCode.ALREADY_PROCESSED_OR_CANCELED);
        }

        // 토스 결제 승인 API 호출
        TossApproveResponse tossResponse = requestTossPaymentApproval(requestDto).block();

        // 결제 승인 -> DB 업데이트
        if (tossResponse != null && "DONE".equals(tossResponse.getStatus())) {
            log.info("[결제 승인 성공] orderId: {}", tossResponse.getOrderId());

            // 쿠폰 할인 적용 및 최종 금액 계산
            BigDecimal originalAmountBD = BigDecimal.valueOf(payment.getOriginalAmount());
            BigDecimal finalAmountBD = originalAmountBD;
            int discountAmount = 0;

            UserCoupon userCoupon = payment.getUserCoupon();
            if (userCoupon != null) {
                if (userCoupon.isUsed()) {
                    throw new BaseException(ErrorCode.COUPON_ALREADY_USED);
                }

                // 새로운 할인 계산 로직 사용
                BigDecimal calculatedDiscount = userCouponService.calculateDiscount(userCoupon.getCoupon(),
                        originalAmountBD);
                discountAmount = calculatedDiscount.intValue();
                finalAmountBD = originalAmountBD.subtract(calculatedDiscount);

                userCoupon.markAsUsed(); // 쿠폰 사용 처리
                log.info(" 쿠폰 할인 적용 - 원가: {}원, 할인: {}원, 최종: {}원",
                        payment.getOriginalAmount(), discountAmount, finalAmountBD.intValue());
            }

            int finalAmount = finalAmountBD.intValue();

            // Payment 업데이트
            payment.updateOnSuccess(tossResponse.getPaymentKey(), tossResponse.getOrderId(),
                    tossResponse.getMethod(), tossResponse.getApprovedAt(), tossResponse.getRequestedAt());

            // 최종 금액 및 할인 금액 업데이트
            payment.updateFinalAmount(finalAmount, discountAmount);

            log.info(" 결제 완료 - 토스 결제: {}원, 실제 차감: {}원",
                    payment.getOriginalAmount(), finalAmount);

            Reservation reservation = reservationRepository.findById((reservationId))
                    .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_ORDER));

            if (reservation.getReservationStatus() != ReservationStatus.REQUESTED) {
                log.warn(" 예약 ID {}의 상태가 PENDING이 아닙니다. 현재 상태: {}. 중복 처리이거나 비정상적인 접근입니다.",
                        reservationId, reservation.getReservationStatus());
                if (reservation.getReservationStatus() == ReservationStatus.PAID) {
                    log.info(" 예약 ID {}는 이미 PAID 상태입니다. 추가 처리 없음.", reservationId);
                } else {
                    throw new BaseException(ErrorCode.INVALID_RESERVATION_STATUS);
                }
            } else {
                reservation.markAsPaid();
                log.info(" 예약 ID {}의 상태가 {}로 변경되었습니다.", reservationId, ReservationStatus.PAID);
                eventPublisher.publishEvent(SaveCreateRoomEvent.from(reservation));
            }

            User mentee = reservation.getMentee();
            if (mentee == null) {
                log.error(" 예약 ID {}에 연결된 멘티(User) 정보를 찾을 수 없습니다.", reservationId);
                throw new BaseException(ErrorCode.NOT_FOUND_ORDER);
            }
            Integer previousTotalPrice = mentee.getTotalPrice();
            Integer newCalculatedTotalPrice = previousTotalPrice + finalAmount;
            mentee.updateTotalPrice(newCalculatedTotalPrice);

            log.info(" 사용자 ID {}의 총 결제 금액이 {}원에서 {}원으로 업데이트되었습니다. (추가 금액: {})",
                    mentee.getId(), previousTotalPrice, mentee.getTotalPrice(), finalAmount);
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
                    .amount(tossResponse.getTotalAmount())
                    .approvedAt(tossResponse.getApprovedAt())
                    .build();
        }
        // 아닐 시 DB 정보 반환
        return PaymentDetailsResponseDto.from(payment);
    }

    // 결제 취소 (환불)
    @Transactional
    public void cancelPayment(Long paymentId, PaymentCancelRequestDto cancelDto, String
            userEmail) {
        // 결제 내역 조회
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BaseException(ErrorCode.PAYMENT_NOT_FOUND));

        if (payment.getReservation().getReservationStartAt().isAfter(LocalDateTime.now().minusHours(2))) {
            throw new BaseException(ErrorCode.PAYMENT_CANCEL_RESERVATION_EXPIRED);
        }

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

            UserCoupon userCoupon = payment.getUserCoupon();
            if (userCoupon != null && userCoupon.isUsed()) {
                userCoupon.unmarkAsUsed();
                log.info("쿠폰 사용 상태 복구 완료 - userCouponId={}", userCoupon.getId());
            }

            Reservation reservation = payment.getReservation();
            reservation.cancel();

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
                            log.error("Toss API 호출 실패: status={}, body={}",
                                    response.statusCode(),
                                    errorBody);
                            return Mono.error(new BaseException(ErrorCode.TOSS_API_FAILED));
                        })).bodyToMono(TossApproveResponse.class);
    }

    // [토스 결제 조회] API 호출
    private Mono<TossPaymentInquiryResponse> requestTossPaymentInquiry(String paymentKey) {
        String url = "https://api.tosspayments.com/v1/payments/" + paymentKey; // 결제 단건 조회 URL
        String authorizations = "Basic " + Base64.getEncoder()
                .encodeToString(
                        (tossSecretKey + ":").getBytes(StandardCharsets.UTF_8)); // 인증 헤더

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
                "https://api.tosspayments.com/v1/payments/" + paymentKey
                        + "/cancel"; // 결제 취소 URL
        String authorizations = "Basic " + Base64.getEncoder()
                .encodeToString(
                        (tossSecretKey + ":").getBytes(StandardCharsets.UTF_8)); // 인증 헤더

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
                            return Mono.error(
                                    new BaseException(ErrorCode.TOSS_CANCEL_API_FAILED));
                        })).bodyToMono(TossCancelResponse.class); // 응답 DTO 변환
    }

    @Transactional(readOnly = true)
    public PagingResponse<PaymentsResponseDto> getPayments(
            UserDetailsImpl userDetails,
            Pageable pageable
    ) {
        Long payerId = userDetails.getId();

        Page<Payment> paymentsPage = paymentRepository.findAllByPayerIdWithDetails(payerId,
                pageable);

        Page<PaymentsResponseDto> dtoPage = paymentsPage.map(payment -> {
            String mentorName = "Unknown Mentor"; // 기본값
            String ticketName = "Unknown Ticket"; // 기본값

            // NullPointerException 방지를 위한 안전한 접근
            if (payment.getReservation() != null
                    && payment.getReservation().getMentor() != null) {
                mentorName = payment.getReservation().getMentor()
                        .getName(); // Assuming User entity has getName()
            }
            if (payment.getTicket() != null) {
                ticketName = payment.getTicket()
                        .getName(); // Assuming Ticket entity has getName()
            }

            // 수정된 DTO의 of 메서드 호출
            return PaymentsResponseDto.of(payment, mentorName, ticketName);
        });

        return PagingResponse.from(dtoPage);
    }
}
