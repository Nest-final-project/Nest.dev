package caffeine.nest_dev.domain.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDto {

    private String nickName;

    private String phoneNumber;

    private String bank;

    private String accountNumber;
}
