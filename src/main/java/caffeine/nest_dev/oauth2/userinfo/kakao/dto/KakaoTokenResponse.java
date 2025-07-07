package caffeine.nest_dev.oauth2.userinfo.kakao.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class) // 카카오 응답이 snake_case 여서 camelCase 와 매칭할 수 있도록
public class KakaoTokenResponse {

    private String accessToken;
    private String refreshToken;

}
