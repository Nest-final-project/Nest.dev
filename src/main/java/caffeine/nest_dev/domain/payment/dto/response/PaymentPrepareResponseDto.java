package caffeine.nest_dev.domain.payment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentPrepareResponseDto {

    private String orderId;         // 주문 번호
    private String orderName;       // 주문 상품
}