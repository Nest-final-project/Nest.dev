package caffeine.nest_dev.domain.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class TokenResponseDto {

    private String accessToken;

    public static TokenResponseDto of(String accessToken) {
        return TokenResponseDto.builder()
                .accessToken(accessToken)
                .build();
    }
}
