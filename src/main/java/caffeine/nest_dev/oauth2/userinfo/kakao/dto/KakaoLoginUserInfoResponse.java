package caffeine.nest_dev.oauth2.userinfo.kakao.dto;

import caffeine.nest_dev.domain.user.enums.SocialType;
import caffeine.nest_dev.oauth2.userinfo.OAuth2UserInfo;
import java.util.Map;
import lombok.Getter;

@Getter
public class KakaoLoginUserInfoResponse extends OAuth2UserInfo {

    public KakaoLoginUserInfoResponse(Map<String, Object> attributes) {
        super(
                SocialType.KAKAO,
                String.valueOf(attributes.get("id")),
                extractEmail(attributes),
                extractNickName(attributes)
        );
    }

    // todo name 파싱 / 추후 사용 예정
//    private static String extractName(Map<String, Object> attributes) {
//        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
//        if (kakaoAccount != null) {
//            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
//            if (profile != null) {
//                return (String) profile.get("name");
//            }
//        }
//        return null;
//    }

    // email 파싱
    private static String extractEmail(Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");

        if (kakaoAccount != null) {
            return (String) kakaoAccount.get("email");
        }
        return null;
    }

    // kakao는 nickname을 profile.nickname 으로 넘겨줌
    private static String extractNickName(Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");

        if (kakaoAccount != null) {
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
            if (profile != null) {
                return (String) profile.get("nickname");
            }
        }
        return null;
    }


}
