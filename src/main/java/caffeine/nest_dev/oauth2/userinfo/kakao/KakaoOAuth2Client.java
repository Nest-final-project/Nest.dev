package caffeine.nest_dev.oauth2.userinfo.kakao;

import caffeine.nest_dev.domain.user.enums.SocialType;
import caffeine.nest_dev.oauth2.client.OAuth2Client;
import caffeine.nest_dev.oauth2.userinfo.OAuth2UserInfo;
import caffeine.nest_dev.oauth2.userinfo.kakao.dto.KakaoLoginUserInfoResponse;
import caffeine.nest_dev.oauth2.userinfo.kakao.dto.KakaoTokenResponse;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class KakaoOAuth2Client implements OAuth2Client {

    private final RestClient restClient;
    private final StringRedisTemplate stringRedisTemplate;

    private final static String AUTH_SERVER_BASE_URL = "https://kauth.kakao.com";
    private final static String RESOURCE_SERVER_BASE_URL = "https://kapi.kakao.com";

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String redirectUrl;

    @Override
    public SocialType getProvider() {
        return SocialType.KAKAO;
    }

    @Override
    public String getAccessToken(String authorizationCode) {
        var body = new LinkedMultiValueMap<String, String>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("redirect_uri", redirectUrl);
        body.add("code", authorizationCode);

        return restClient.post()
                .uri(AUTH_SERVER_BASE_URL + "/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, resp) -> {
                    throw new RuntimeException("카카오 AccessToken 조회 실패");
                })
                .body(KakaoTokenResponse.class)
                .getAccessToken();
    }

    @Override
    public String generateLoginPageUrl() {
        // state 랜덤 값으로 생성
        String state = UUID.randomUUID().toString();

        // redis에 저장
        stringRedisTemplate.opsForValue().set("oauth2:state" + state, "Y", 5, TimeUnit.MINUTES);

        return AUTH_SERVER_BASE_URL
                + "/oauth/authorize"
                + "?client_id=" + clientId
                + "&redirect_uri=" + redirectUrl
                + "&response_type=" + "code"
                + "&state=" + state;
    }

    @Override
    public OAuth2UserInfo retrieveUserInfo(String accessToken) {
        Map<String, Object> attributes = restClient.get()
                .uri(RESOURCE_SERVER_BASE_URL + "/v2/user/me")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, resp) -> {
                    throw new RuntimeException("카카오 UserInfo 조회 실패");
                })
                .body(Map.class); // Map 클래스는 제네릭 타입인데 타입을 지정해줘서 생기는 경고 (무시해도 런타임에 문제 X)

        return new KakaoLoginUserInfoResponse(attributes);
    }
}
