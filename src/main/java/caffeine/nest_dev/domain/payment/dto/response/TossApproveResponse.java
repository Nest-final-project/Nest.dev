package caffeine.nest_dev.domain.payment.dto.response;

import caffeine.nest_dev.domain.payment.enums.TossPaymentMethod;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class TossApproveResponse {

    private String paymentKey;          // 고유 결제 key 값
    private String orderId;             // 주문 번호
    private String status;              // 결제 처리 상태
    private Integer totalAmount;        // 총 결제 금액
    private TossPaymentMethod method;   // 결제 수단
    private String requestedAt;         // 결제 요청 시각
    private String approvedAt;          // 결제 승인 시각
}
