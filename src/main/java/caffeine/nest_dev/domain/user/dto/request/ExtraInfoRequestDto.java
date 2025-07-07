package caffeine.nest_dev.domain.user.dto.request;

import caffeine.nest_dev.domain.user.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExtraInfoRequestDto {

    private Long id;

    @NotBlank(message = "이름은 필수 입니다.")
    private String name;

    @NotBlank(message = "전화번호는 필수입니다.")
    @Pattern(regexp = "^\\d{3}-\\d{4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다.")
    private String phoneNumber;

    @NotNull(message = "역할 입력은 필수입니다.")
    private UserRole userRole;

}
