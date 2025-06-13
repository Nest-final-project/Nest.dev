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
    private String email;
    private String nickName;
    private boolean isNew;
    private String accessToken;
    private String refreshToken;

    public static OAuth2LoginResponseDto of(User user, String accessToken, String refreshToken, boolean isNew) {
        return OAuth2LoginResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickName(user.getNickName())
                .isNew(isNew)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
