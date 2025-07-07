package caffeine.nest_dev.oauth2.dto.response;

import caffeine.nest_dev.domain.user.entity.User;
import caffeine.nest_dev.domain.user.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OAuth2LoginResponseDto {

    private Long id;
    private String email;
    private String nickName;
    private UserRole userRole;
    private boolean newUser;
    private String accessToken;
    private String refreshToken;

    public static OAuth2LoginResponseDto of(User user, String accessToken, String refreshToken, boolean newUser) {
        return OAuth2LoginResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickName(user.getNickName())
                .userRole(user.getUserRole())
                .newUser(newUser)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
