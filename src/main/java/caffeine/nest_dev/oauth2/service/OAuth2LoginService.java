package caffeine.nest_dev.oauth2.service;

import caffeine.nest_dev.common.config.JwtUtil;
import caffeine.nest_dev.common.enums.ErrorCode;
import caffeine.nest_dev.common.exception.BaseException;
import caffeine.nest_dev.domain.auth.repository.TokenRepository;
import caffeine.nest_dev.domain.auth.service.AuthService;
import caffeine.nest_dev.domain.user.entity.User;
import caffeine.nest_dev.domain.user.enums.SocialType;
import caffeine.nest_dev.oauth2.client.OAuth2ClientService;
import caffeine.nest_dev.oauth2.dto.response.OAuth2LoginResponseDto;
import caffeine.nest_dev.oauth2.userinfo.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class OAuth2LoginService {

    private final JwtUtil jwtUtil;
    private final OAuth2ClientService oAuth2ClientService;
    private final TokenRepository tokenRepository;
    private final AuthService authService;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedisTemplate<String, Object> objectRedisTemplate;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;
    @Value("60000")
    private long socialInfoExpresion;

    private static final String SUFFIX = "SOCIAL";

    public String generateLoginPageUrl(SocialType provider) {
        return oAuth2ClientService.generateLoginPageUrl(provider);
    }

    public String login(SocialType provider, String authorizationCode,
            String state) {

        // state 값 유효성 검증
        String storedState = stringRedisTemplate.opsForValue().get("oauth2:state" + state);
        if (storedState == null) {
            throw new BaseException(ErrorCode.INVALID_STATE);
        }

        // 검증 후 redis에서 삭제
        stringRedisTemplate.delete("oauth2:state" + state);

        // 소셜 제공자로부터 사용자 정보 조회
        OAuth2UserInfo userInfo = oAuth2ClientService.getUserInfo(provider, authorizationCode);

        // DB에서 사용자 조회 (없으면 등록)
        User user = authService.findUserByEmail(userInfo, provider);

        // 검증 후 null 값일때 반환 (신규 유저)
        if (user.getUserRole() == null || user.getPhoneNumber() == null || user.getName() == null) {
            // 임시 토큰 발급
            String tempAccessToken = jwtUtil.createTempAccessToken(user);

            // redis 에 로그인된 유저의 정보 담기(토큰, 정보, isNew 등)
            OAuth2LoginResponseDto dto = OAuth2LoginResponseDto.of(user, tempAccessToken, null,
                    true);
            tokenRepository.saveSocial(dto.getId(), dto, socialInfoExpresion);

            // Redis에 DTO를 저장할 때 사용한 userId를 일회성 코드로 사용
            String oneTimeCode = String.valueOf(dto.getId());

            String redirectUrl = "https://www.nest-dev.click/oauth2/callback";
            String finalRedirectUrl = UriComponentsBuilder.fromUriString(redirectUrl)
                    .queryParam("code", SUFFIX + oneTimeCode).build()
                    .toUriString();

            return finalRedirectUrl;
        }

        // accessToken 발급
        String accessToken = jwtUtil.createAccessToken(user);
        // refreshToken 발급
        String refreshToken = jwtUtil.createRefreshToken(user);

        // redis 에 refreshToken 저장
        tokenRepository.save(user.getId(), refreshToken, refreshTokenExpiration);

        // redis 에 로그인된 유저의 정보 담기(토큰, 정보, isNew 등)
        OAuth2LoginResponseDto dto = OAuth2LoginResponseDto.of(user, accessToken,
                refreshToken, false);
        tokenRepository.saveSocial(dto.getId(), dto, socialInfoExpresion);

        // Redis에 DTO를 저장할 때 사용한 userId를 일회성 코드로 사용
        String oneTimeCode = String.valueOf(dto.getId());

        String redirectUrl = "https://www.nest-dev.click/oauth2/callback";
        String finalRedirectUrl = UriComponentsBuilder.fromUriString(redirectUrl)
                .queryParam("code", SUFFIX + oneTimeCode).build()
                .toUriString();

        return finalRedirectUrl;
    }

    public OAuth2LoginResponseDto loginCheck(String code) {
        // code로 redis에서 값 불러오기
        OAuth2LoginResponseDto dto = (OAuth2LoginResponseDto) objectRedisTemplate.opsForValue()
                .get(code);

        if (dto == null) {
            throw new BaseException(ErrorCode.DTO_NOT_FOUND);
        }

        // code 에서 접두사 뺀 값과 userId 가 일치하는지 확인
        String replace = code.replace(SUFFIX, "");
        if (!dto.getId().toString().equals(replace)) {
            throw new BaseException(ErrorCode.INVALID_SOCIAL_CODE);
        }

        return dto;
    }
}
