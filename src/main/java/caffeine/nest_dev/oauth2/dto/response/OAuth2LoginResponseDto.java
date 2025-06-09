package caffeine.nest_dev.oauth2.dto.response;

import caffeine.nest_dev.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class OAuth2LoginResponseDto {

    private Long id;
    private String name;
    private String email;
    private String accessToken;
    private String refreshToken;

    public static OAuth2LoginResponseDto of(User user, String accessToken, String refreshToken) {
        return OAuth2LoginResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
