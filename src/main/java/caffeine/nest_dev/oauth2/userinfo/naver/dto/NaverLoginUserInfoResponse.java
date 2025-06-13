package caffeine.nest_dev.oauth2.userinfo.naver.dto;

import caffeine.nest_dev.domain.user.enums.SocialType;
import caffeine.nest_dev.oauth2.userinfo.OAuth2UserInfo;
import java.util.Map;

public class NaverLoginUserInfoResponse extends OAuth2UserInfo {


    public NaverLoginUserInfoResponse(Map<String, Object> attributes) {
        super(
                SocialType.NAVER,
                extractId(attributes),
                extractEmail(attributes),
                extractNickname(attributes)
        );
    }

    // 고유 id 파싱
    private static String extractId(Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        return response != null ? (String) response.get("id") : null;
    }

    // 이름 파싱
//    private static String extractName(Map<String, Object> attributes) {
//        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
//        return response != null ? (String) response.get("name") : null;
//    }

    // 이메일 파싱
    private static String extractEmail(Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        return response != null ? (String) response.get("email") : null;
    }

    // 닉네임 파싱
    private static String extractNickname(Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        return response != null ? (String) response.get("nickname") : null;
    }
}
