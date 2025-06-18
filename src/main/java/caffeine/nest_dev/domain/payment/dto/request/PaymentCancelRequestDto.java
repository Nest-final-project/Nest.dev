package caffeine.nest_dev.domain.payment.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentCancelRequestDto {

    private String cancelReason;    // 취소 사유
}