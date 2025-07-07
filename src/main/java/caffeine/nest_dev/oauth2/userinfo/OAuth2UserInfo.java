package caffeine.nest_dev.oauth2.userinfo;

import caffeine.nest_dev.domain.user.enums.SocialType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OAuth2UserInfo {

    private SocialType provider;
    private String id;
    private String email;
    private String nickName;
}
