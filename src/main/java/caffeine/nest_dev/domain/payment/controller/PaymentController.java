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

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/prepare")
    public ResponseEntity<CommonResponse<PaymentPrepareResponseDto>> prepare(
            @RequestBody PaymentPrepareRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String userEmail = userDetails.getEmail();
        PaymentPrepareResponseDto responseDto = paymentService.preparePayment(requestDto,
                userEmail);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.of(SuccessCode.SUCCESS_PAYMENT_PREPARE, responseDto));
    }

    @PostMapping("/confirm")
    public ResponseEntity<CommonResponse<PaymentConfirmResponseDto>> confirmPayment(
            @RequestBody PaymentConfirmRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam("reservationId") Long reservationId) {
        String userEmail = userDetails.getEmail();
        PaymentConfirmResponseDto responseDto = paymentService.confirmPayment(requestDto, userEmail,
                reservationId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_PAYMENT_OK, responseDto));
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<CommonResponse<PaymentDetailsResponseDto>> getPaymentDetails(
            @PathVariable Long paymentId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String userEmail = userDetails.getEmail();
        PaymentDetailsResponseDto responseDto = paymentService.getPaymentDetails(paymentId,
                userEmail);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_PAYMENT_LIST_READ, responseDto));
    }

    @PostMapping("/{paymentId}/cancel")
    public ResponseEntity<CommonResponse<Void>> cancelPayment(@PathVariable Long paymentId,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody PaymentCancelRequestDto requestDto) {
        String userEmail = userDetails.getEmail();
        paymentService.cancelPayment(paymentId, requestDto, userEmail);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_PAYMENT_CANCEL));
    }


    // 결제 내역 조회
    @GetMapping
    public ResponseEntity<CommonResponse<PagingResponse<PaymentsResponseDto>>> getPayments(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PageableDefault Pageable pageable
    ) {
        PagingResponse<PaymentsResponseDto> payments = paymentService.getPayments(userDetails,
                pageable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_PAYMENT_LIST_READ, payments));
    }
}
