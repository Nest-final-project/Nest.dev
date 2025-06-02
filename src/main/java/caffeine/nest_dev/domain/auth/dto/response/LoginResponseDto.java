package caffeine.nest_dev.domain.auth.dto.response;

import caffeine.nest_dev.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LoginResponseDto {
    private Long id;
    private String email;
    private String nickName;
    private String accessToken;
    private String refreshToken;

    public static LoginResponseDto of(User user, String accessToken, String refreshToken) {
        return LoginResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickName(user.getNickName())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
