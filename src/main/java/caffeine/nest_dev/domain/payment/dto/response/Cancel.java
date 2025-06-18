package caffeine.nest_dev.domain.payment.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Cancel {
    private Integer cancelAmount;   // 취소 금액
    private String cancelReason;    // 취소 사유
    private String canceledAt;      // 취소된 시각
}