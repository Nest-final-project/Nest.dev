package caffeine.nest_dev.domain.payment.dto.response;

import caffeine.nest_dev.domain.payment.entity.Payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PaymentConfirmResponseDto {

    private final String paymentKey;    // 고유 결제 key 값
    private final String orderId;       // 주문 번호
    private final String status;        // 결제 처리 상태
    private final Integer amount;       // 결제 금액

    public static PaymentConfirmResponseDto of(Payment payment) {
        return PaymentConfirmResponseDto.builder()
                .paymentKey(payment.getPaymentKey())
                .orderId(String.valueOf(payment.getReservation().getId()))
                .amount(payment.getAmount())
                .status(payment.getStatus().getDescription())
                .build();
    }
}
