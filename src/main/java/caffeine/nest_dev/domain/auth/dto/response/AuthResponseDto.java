package caffeine.nest_dev.domain.auth.dto.response;

import caffeine.nest_dev.domain.user.entity.User;
import caffeine.nest_dev.domain.user.enums.UserGrade;
import caffeine.nest_dev.domain.user.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AuthResponseDto {

    private Long id;
    private String name;
    private String email;
    private String nickName;
    private String phoneNumber;
    private UserGrade userGrade;
    private UserRole userRole;

    public static AuthResponseDto of(User user) {
        return AuthResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .nickName(user.getNickName())
                .phoneNumber(user.getPhoneNumber())
                .userGrade(user.getUserGrade())
                .userRole(user.getUserRole())
                .build();
    }
}
