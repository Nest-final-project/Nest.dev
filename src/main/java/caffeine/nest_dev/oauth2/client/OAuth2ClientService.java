package caffeine.nest_dev.oauth2.client;

import caffeine.nest_dev.common.enums.ErrorCode;
import caffeine.nest_dev.common.exception.BaseException;
import caffeine.nest_dev.domain.user.enums.SocialType;
import caffeine.nest_dev.oauth2.userinfo.OAuth2UserInfo;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class OAuth2ClientService {

    private final Map<SocialType, OAuth2Client> oAuth2Clients;

    public OAuth2ClientService(List<OAuth2Client> oAuth2Clients) {
        this.oAuth2Clients = oAuth2Clients.stream().collect(Collectors.toMap(
                OAuth2Client::getProvider, Function.identity()));
    }

    // SocialType에 해당하는 로그인페이지 URL 불러오기
    public String generateLoginPageUrl(SocialType provider) {

        // SocialType에 해당하는 OAuth2Client 객체 만들기
        OAuth2Client oAuth2Client = this.create(provider);

        return oAuth2Client.generateLoginPageUrl();
    }

    // SocialType 과 code 로 유저의 정보 가져오기
    public OAuth2UserInfo getUserInfo(SocialType provider, String authorizationCode) {

        OAuth2Client oAuth2Client = this.create(provider);

        // code 로 accessToken 가져오기(provider 가 제공하는 토큰)
        String accessToken = oAuth2Client.getAccessToken(authorizationCode);

        // 토큰으로 소셜 제공자의 사용자 정보 가져오기
        return oAuth2Client.retrieveUserInfo(accessToken);
    }

    // SocialType으로 OAuth2Client 객체 만들기
    private OAuth2Client create(SocialType provider) {
        return Optional.ofNullable(oAuth2Clients.get(provider)).orElseThrow(() -> new BaseException(
                ErrorCode.INVALID_SOCIAL_TYPE));
    }
}
