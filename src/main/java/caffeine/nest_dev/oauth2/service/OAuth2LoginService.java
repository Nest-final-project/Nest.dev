package caffeine.nest_dev.oauth2.service;

import caffeine.nest_dev.common.config.JwtUtil;
import caffeine.nest_dev.common.config.PasswordEncoder;
import caffeine.nest_dev.domain.auth.repository.RefreshTokenRepository;
import caffeine.nest_dev.domain.user.entity.User;
import caffeine.nest_dev.domain.user.enums.SocialType;
import caffeine.nest_dev.domain.user.enums.UserGrade;
import caffeine.nest_dev.domain.user.enums.UserRole;
import caffeine.nest_dev.domain.user.repository.UserRepository;
import caffeine.nest_dev.oauth2.client.OAuth2ClientService;
import caffeine.nest_dev.oauth2.dto.response.OAuth2LoginResponseDto;
import caffeine.nest_dev.oauth2.userinfo.OAuth2UserInfo;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OAuth2LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final OAuth2ClientService oAuth2ClientService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    public String generateLoginPageUrl(SocialType provider) {
        return oAuth2ClientService.generateLoginPageUrl(provider);
    }

    @Transactional
    public OAuth2LoginResponseDto login(SocialType provider, String authorizationCode) {

        // 소셜 제공자로부터 사용자 정보 조회
        OAuth2UserInfo userInfo = oAuth2ClientService.getUserInfo(provider, authorizationCode);

        // DB에서 사용자 조회 (없으면 등록)
        User user = userRepository.findByEmailAndIsDeletedFalse(userInfo.getEmail())
                .orElseGet(() -> registerIfAbsent(userInfo, provider));

        // accessToken 발급
        String accessToken = jwtUtil.createAccessToken(user);
        // refreshToken 발급
        String refreshToken = jwtUtil.createRefreshToken(user);

        // redis 에 refreshToken 저장
        refreshTokenRepository.save(user.getId(), refreshToken, refreshTokenExpiration);


        return OAuth2LoginResponseDto.of(user, accessToken, refreshToken);
    }

    // OAuth2UserInfo 정보로 user 객체 만들기
    private User registerIfAbsent(OAuth2UserInfo userInfo, SocialType provider) {
        return userRepository.save(User.builder()
                .name(userInfo.getName())
                .email(userInfo.getEmail())
                .phoneNumber(userInfo.getPhoneNumber())
                .password(passwordEncoder.encode(UUID.randomUUID().toString())) // 임의의 비밀번호 사용
                .socialType(provider)
                .socialId(userInfo.getId())
                .userRole(UserRole.MENTEE)
                .userGrade(UserGrade.SEED)
                .build());
    }
}
