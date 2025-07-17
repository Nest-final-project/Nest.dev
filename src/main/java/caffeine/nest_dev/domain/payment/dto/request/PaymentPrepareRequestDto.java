package caffeine.nest_dev.domain.payment.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentPrepareRequestDto {

    private String reservationId;   // 예약 고유 식별자
    private Long ticketId;          // 티켓 고유 식별자
    private Long couponId;
    private Long userId;
    private String orderName;       // 주문 상품
    private Integer amount;         // 결제 금액
    private String customerName;    // 구매자 명
    private String successUrl;      // 결제 성공시 이동할 url
    private String failUrl;         // 결제 실패시 이동할 url
}