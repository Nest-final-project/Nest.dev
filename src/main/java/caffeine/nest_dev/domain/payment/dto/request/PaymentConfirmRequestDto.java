package caffeine.nest_dev.domain.payment.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentConfirmRequestDto {

    @NotBlank(message = "결제 키는 필수입니다")
    @Size(min = 20, max = 200, message = "결제 키 형식이 올바르지 않습니다")
    private String paymentKey;      // 고유 결제 key 값
    
    @NotBlank(message = "주문 ID는 필수입니다")
    @Size(min = 6, max = 64, message = "주문 ID 형식이 올바르지 않습니다")
    private String orderId;         // 주문 번호
    
    @NotNull(message = "결제 금액은 필수입니다")
    @Min(value = 100, message = "최소 결제 금액은 100원입니다")
    @Max(value = 10000000, message = "최대 결제 금액은 1,000만원입니다")
    private Integer amount;         // 결제 금액
    
    @NotNull(message = "예약 ID는 필수입니다")
    @Positive(message = "예약 ID는 양수여야 합니다")
    private Long reservationId;     // 예약 ID (프론트엔드에서 전달)
}
