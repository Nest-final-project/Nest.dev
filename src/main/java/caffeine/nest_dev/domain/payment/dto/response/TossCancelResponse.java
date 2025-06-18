package caffeine.nest_dev.domain.payment.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TossCancelResponse {

    private String paymentKey;  // 고유 결제 key 값
    private String orderId;     // 주문 번호
    private String status;      // 결제 처리 상태
    private Cancel[] cancels;   // 취소 이력 배열
}