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

    @NotBlank(message = "예약 ID는 필수입니다")
    private String reservationId;   // 예약 고유 식별자
    
    @NotNull(message = "티켓 ID는 필수입니다")
    @Positive(message = "티켓 ID는 양수여야 합니다")
    private Long ticketId;          // 티켓 고유 식별자
    
    @Positive(message = "쿠폰 ID는 양수여야 합니다")
    private Long couponId;
    
    @Positive(message = "사용자 ID는 양수여야 합니다")
    private Long userId;
    
    @NotBlank(message = "주문명은 필수입니다")
    @Size(max = 100, message = "주문명은 100자 이하여야 합니다")
    private String orderName;       // 주문 상품
    
    @NotNull(message = "결제 금액은 필수입니다")
    @Min(value = 100, message = "최소 결제 금액은 100원입니다")
    @Max(value = 10000000, message = "최대 결제 금액은 1,000만원입니다")
    private Integer amount;         // 결제 금액
    
    @NotBlank(message = "구매자명은 필수입니다")
    @Size(min = 2, max = 50, message = "구매자명은 2-50자 사이여야 합니다")
    @Pattern(regexp = "^[가-힣a-zA-Z\\s]+$", message = "구매자명은 한글, 영문만 가능합니다")
    private String customerName;    // 구매자 명
    
    @NotBlank(message = "성공 URL은 필수입니다")
    @URL(message = "올바른 URL 형식이어야 합니다")
    private String successUrl;      // 결제 성공시 이동할 url
    
    @NotBlank(message = "실패 URL은 필수입니다")
    @URL(message = "올바른 URL 형식이어야 합니다")
    private String failUrl;         // 결제 실패시 이동할 url
}