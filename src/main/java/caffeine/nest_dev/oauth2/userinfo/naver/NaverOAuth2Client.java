package caffeine.nest_dev.oauth2.userinfo.naver;

import caffeine.nest_dev.domain.user.enums.SocialType;
import caffeine.nest_dev.oauth2.client.OAuth2Client;
import caffeine.nest_dev.oauth2.userinfo.OAuth2UserInfo;
import caffeine.nest_dev.oauth2.userinfo.naver.dto.NaverLoginUserInfoResponse;
import caffeine.nest_dev.oauth2.userinfo.naver.dto.NaverTokenResponse;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class NaverOAuth2Client implements OAuth2Client {

    private final RestClient restClient;
    private final StringRedisTemplate stringRedisTemplate;

    private static final String AUTH_SERVER_BASE_URL = "https://nid.naver.com";
    private static final String RESOURCE_SERVER_BASE_URL = "https://openapi.naver.com";

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String clientSecret;
    @Value("${spring.security.oauth2.client.registration.naver.redirect-uri}")
    private String redirectUrl;

    @Override
    public SocialType getProvider() {
        return SocialType.NAVER;
    }

    @Override
    public String getAccessToken(String authorizationCode) {
        var body = new LinkedMultiValueMap<String, String>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("code", authorizationCode);
        body.add("redirect_uri", redirectUrl);
            return restClient.post()
                    .uri(AUTH_SERVER_BASE_URL + "/oauth2.0/token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(body)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (req, resp) -> {
                        throw new RuntimeException("네이버 AccessToken 조회 실패");
                    })
                    .body(NaverTokenResponse.class)
                    .getAccessToken();


    }

    @Override
    public String generateLoginPageUrl() {
        // state 생성
        String state = UUID.randomUUID().toString();

        // redis에 저장
        stringRedisTemplate.opsForValue().set("oauth2:state" + state, "Y", 5, TimeUnit.MINUTES);

        return AUTH_SERVER_BASE_URL
                + "/oauth2.0/authorize"
                + "?client_id=" + clientId
                + "&response_type=code"
                + "&redirect_uri=" + redirectUrl
                + "&state=" + state; // CSRF 방지용 state 값 권장;
    }

    @Override
    public OAuth2UserInfo retrieveUserInfo(String accessToken) {
        Map<String, Object> attributes = restClient.get()
                .uri(RESOURCE_SERVER_BASE_URL + "/v1/nid/me")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, resp) -> {
                    throw new RuntimeException("네이버 UserInfo 조회 실패");
                })
                .body(Map.class);

        return new NaverLoginUserInfoResponse(attributes);
    }
}
