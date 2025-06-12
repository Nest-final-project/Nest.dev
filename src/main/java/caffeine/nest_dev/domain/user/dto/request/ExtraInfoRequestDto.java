package caffeine.nest_dev.domain.user.dto.request;

import caffeine.nest_dev.domain.user.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExtraInfoRequestDto {

    private String name;
    private String phoneNumber;
    private UserRole userRole;

}
