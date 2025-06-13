package caffeine.nest_dev.oauth2.client;

import caffeine.nest_dev.domain.user.enums.SocialType;
import caffeine.nest_dev.oauth2.userinfo.OAuth2UserInfo;

public interface OAuth2Client {

    SocialType getProvider();

    String getAccessToken(String authorizationCode);

    String generateLoginPageUrl();

    OAuth2UserInfo retrieveUserInfo(String accessToken);
}
