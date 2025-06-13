package caffeine.nest_dev.domain.auth.dto.request;

import caffeine.nest_dev.domain.user.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequestDto {

    @NotBlank(message = "이름은 필수 입니다.")
    private String name;

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "닉네임은 필수입니다.")
    private String nickName;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*\\W).{8,}$", message = "비밀번호 형식이 올바르지 않습니다.")
    private String password;

    @NotBlank(message = "전화번호는 필수입니다.")
    @Pattern(regexp = "^\\d{3}-\\d{4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다.")
    private String phoneNumber;

    @NotNull(message = "역할 입력은 필수입니다.")
    private UserRole userRole;

//    public User toEntity(String encodedPassword) {
//        return User.builder()
//                .name(name)
//                .email(email)
//                .nickName(nickName)
//                .password(encodedPassword)
//                .phoneNumber(phoneNumber)
//                .userRole(userRole)
//                .userGrade(UserGrade.SEED)
//                .totalPrice(0)
//                .build();
//    }
}
