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

    private String paymentKey;      // 고유 결제 key 값
    private String orderId;         // 주문 번호
    private Integer amount;         // 결제 금액
    private Long reservationId;     // 예약 ID (프론트엔드에서 전달)
}
