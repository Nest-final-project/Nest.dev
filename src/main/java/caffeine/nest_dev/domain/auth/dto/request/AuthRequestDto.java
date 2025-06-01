package caffeine.nest_dev.domain.auth.dto.request;

import caffeine.nest_dev.domain.user.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthRequestDto {

    @NotBlank(message = "이름은 필수 입니다.")
    private final String name;

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private final String email;

    @NotBlank(message = "닉네임은 필수입니다.")
    private final String nickname;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*\\W).{8,}$", message = "비밀번호 형식이 올바르지 않습니다.")
    private final String password;

    @NotBlank(message = "전화번호는 필수입니다.")
    @Pattern(regexp = "^\\d{3}-\\d{4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다.")
    private final String phoneNumber;

    @NotBlank(message = "회원 구분은 필수입니다.")
    private final UserRole userRole;
}
