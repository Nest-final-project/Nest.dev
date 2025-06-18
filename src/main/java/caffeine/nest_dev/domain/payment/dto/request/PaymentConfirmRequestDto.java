package caffeine.nest_dev.domain.payment.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class PaymentConfirmRequestDto {

    private String paymentKey;      // 고유 결제 key 값
    private String orderId;         // 주문 번호
    private Integer amount;         // 결제 금액
}
