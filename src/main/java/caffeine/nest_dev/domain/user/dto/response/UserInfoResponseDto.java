package caffeine.nest_dev.domain.user.dto.response;

import caffeine.nest_dev.domain.user.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UserInfoResponseDto {

    private Long id;
    private String name;
    private UserRole userRole;

    public static UserInfoResponseDto of(Long id, String name, UserRole userRole) {
        return UserInfoResponseDto.builder()
                .id(id)
                .name(name)
                .userRole(userRole)
                .build();
    }

}
