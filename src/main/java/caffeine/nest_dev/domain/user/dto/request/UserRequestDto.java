package caffeine.nest_dev.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDto {

    @Email
    private String email;

    private String nickName;

    private String phoneNumber;

    private String bank;

    private String accountNumber;
}
