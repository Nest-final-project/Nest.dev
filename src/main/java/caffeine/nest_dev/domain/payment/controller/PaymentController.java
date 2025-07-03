package caffeine.nest_dev.domain.payment.controller;

import caffeine.nest_dev.common.dto.CommonResponse;
import caffeine.nest_dev.common.dto.PagingResponse;
import caffeine.nest_dev.common.enums.SuccessCode;
import caffeine.nest_dev.domain.payment.dto.request.PaymentCancelRequestDto;
import caffeine.nest_dev.domain.payment.dto.request.PaymentConfirmRequestDto;
import caffeine.nest_dev.domain.payment.dto.request.PaymentPrepareRequestDto;
import caffeine.nest_dev.domain.payment.dto.response.PaymentConfirmResponseDto;
import caffeine.nest_dev.domain.payment.dto.response.PaymentDetailsResponseDto;
import caffeine.nest_dev.domain.payment.dto.response.PaymentPrepareResponseDto;
import caffeine.nest_dev.domain.payment.dto.response.PaymentsResponseDto;
import caffeine.nest_dev.domain.payment.service.PaymentService;
import caffeine.nest_dev.domain.user.entity.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Payment", description = "결제 관리 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "결제 준비", description = "결제를 준비하고 결제 정보를 생성합니다")
    @ApiResponse(responseCode = "201", description = "결제 준비 성공")
    @PostMapping("/prepare")
    public ResponseEntity<CommonResponse<PaymentPrepareResponseDto>> prepare(
            @Parameter(description = "결제 준비 요청 정보") @RequestBody PaymentPrepareRequestDto requestDto,
            @Parameter(description = "인증된 사용자 정보") @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String userEmail = userDetails.getEmail(); 
        PaymentPrepareResponseDto responseDto = paymentService.preparePayment(requestDto,
                userEmail);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.of(SuccessCode.SUCCESS_PAYMENT_PREPARE, responseDto));
    }

    @Operation(summary = "결제 승인", description = "결제를 최종 승인 처리합니다")
    @ApiResponse(responseCode = "200", description = "결제 승인 성공")
    @PostMapping("/confirm")
    public ResponseEntity<CommonResponse<PaymentConfirmResponseDto>> confirmPayment(
            @Parameter(description = "결제 승인 요청 정보") @RequestBody PaymentConfirmRequestDto requestDto,
            @Parameter(description = "인증된 사용자 정보") @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String userEmail = userDetails.getEmail();
        PaymentConfirmResponseDto responseDto = paymentService.confirmPayment(requestDto, userEmail,
                requestDto.getReservationId());
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_PAYMENT_OK, responseDto));
    }

    @Operation(summary = "결제 상세 조회", description = "특정 결제의 상세 정보를 조회합니다")
    @ApiResponse(responseCode = "200", description = "결제 상세 조회 성공")
    @GetMapping("/{paymentId}")
    public ResponseEntity<CommonResponse<PaymentDetailsResponseDto>> getPaymentDetails(
            @Parameter(description = "결제 ID") @PathVariable Long paymentId, 
            @Parameter(description = "인증된 사용자 정보") @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String userEmail = userDetails.getEmail();
        PaymentDetailsResponseDto responseDto = paymentService.getPaymentDetails(paymentId,
                userEmail);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_PAYMENT_LIST_READ, responseDto));
    }

    @Operation(summary = "결제 취소", description = "결제를 취소합니다")
    @ApiResponse(responseCode = "200", description = "결제 취소 성공")
    @PostMapping("/{paymentId}/cancel")
    public ResponseEntity<CommonResponse<Void>> cancelPayment(
            @Parameter(description = "취소할 결제 ID") @PathVariable Long paymentId,
            @Parameter(description = "인증된 사용자 정보") @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Parameter(description = "결제 취소 요청 정보") @RequestBody PaymentCancelRequestDto requestDto) {
        String userEmail = userDetails.getEmail();
        paymentService.cancelPayment(paymentId, requestDto, userEmail);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_PAYMENT_CANCEL));
    }


    // 결제 내역 조회
    @Operation(summary = "결제 내역 조회", description = "사용자의 결제 내역을 페이징하여 조회합니다")
    @ApiResponse(responseCode = "200", description = "결제 내역 조회 성공")
    @GetMapping
    public ResponseEntity<CommonResponse<PagingResponse<PaymentsResponseDto>>> getPayments(
            @Parameter(description = "인증된 사용자 정보") @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Parameter(description = "페이지 정보") @PageableDefault Pageable pageable
    ) {
        PagingResponse<PaymentsResponseDto> payments = paymentService.getPayments(userDetails,
                pageable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_PAYMENT_LIST_READ, payments));
    }
}
