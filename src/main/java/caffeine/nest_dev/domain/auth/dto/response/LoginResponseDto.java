package caffeine.nest_dev.domain.auth.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class LoginResponseDto {
    private final Long userId;
    private final String email;
    private final String nickName;
    private final String accessToken;

}
