package caffeine.nest_dev.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePasswordRequestDto {

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*\\W).{8,}$", message = "비밀번호 형식이 올바르지 않습니다.")
    private String rawPassword;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*\\W).{8,}$", message = "비밀번호 형식이 올바르지 않습니다.")
    private String newPassword;

}
