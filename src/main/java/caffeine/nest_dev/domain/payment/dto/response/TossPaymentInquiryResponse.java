package caffeine.nest_dev.domain.payment.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TossPaymentInquiryResponse {

    private String version;         // Payment 객체 응답 버전
    private String paymentKey;      // 고유 결제 key 값
    private String orderId;         // 주문 번호
    private String status;          // 결제 처리 상태
    private String method;          // 결제 수단
    private Integer totalAmount;    // 총 결제 금액
    private String requestedAt;     // 결제 요청 시각
    private String approvedAt;      // 결제 승인 시각
}