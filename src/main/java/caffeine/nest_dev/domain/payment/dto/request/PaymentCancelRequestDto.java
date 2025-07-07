package caffeine.nest_dev.domain.payment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentCancelRequestDto {

    @NotBlank(message = "취소 사유는 필수입니다")
    @Size(min = 10, max = 255, message = "취소 사유는 10-255 사이여야 합니다")
    @Pattern(regexp = "^[가-힣a-zA-Z0-9\\s\\.,!?()-]+$", 
             message = "취소 사유에 특수문자는 사용할 수 없습니다")
    private String cancelReason;    // 취소 사유
}