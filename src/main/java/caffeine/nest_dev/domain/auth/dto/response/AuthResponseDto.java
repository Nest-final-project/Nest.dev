package caffeine.nest_dev.domain.auth.dto.response;

import caffeine.nest_dev.domain.user.enums.UserGrade;
import caffeine.nest_dev.domain.user.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AuthResponseDto {

    private final Long id;
    private final String name;
    private final String email;
    private final String nickName;
    private final String phoneNumber;
    private final UserGrade userGrade;
    private final UserRole userRole;
}
