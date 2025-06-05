package caffeine.nest_dev.domain.auth.service;

import caffeine.nest_dev.common.config.JwtUtil;
import caffeine.nest_dev.common.config.PasswordEncoder;
import caffeine.nest_dev.common.enums.ErrorCode;
import caffeine.nest_dev.common.exception.BaseException;
import caffeine.nest_dev.domain.auth.dto.request.AuthRequestDto;
import caffeine.nest_dev.domain.auth.dto.request.LoginRequestDto;
import caffeine.nest_dev.domain.auth.dto.request.RefreshTokenRequestDto;
import caffeine.nest_dev.domain.auth.dto.response.AuthResponseDto;
import caffeine.nest_dev.domain.auth.dto.response.LoginResponseDto;
import caffeine.nest_dev.domain.auth.dto.response.TokenResponseDto;
import caffeine.nest_dev.domain.auth.repository.RefreshTokenRepository;
import caffeine.nest_dev.domain.user.entity.User;
import caffeine.nest_dev.domain.user.repository.UserRepository;
import caffeine.nest_dev.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final StringRedisTemplate stringRedisTemplate;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    @Transactional
    public AuthResponseDto signup(AuthRequestDto dto) {

        // 이메일 중복 검증
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new BaseException(ErrorCode.ALREADY_EXIST_EMAIL);
        }

        // 비밀번호 인코딩
        String encoded = passwordEncoder.encode(dto.getPassword());

        User user = dto.toEntity(encoded);

        User savedUser = userRepository.save(user);

        return AuthResponseDto.of(savedUser);
    }

    @Transactional
    public LoginResponseDto login(LoginRequestDto dto) {
        // 유저 조회
        User user = userRepository.findByEmailAndIsDeletedFalse(dto.getEmail())
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_USER));

        // 비밀번호 일치 여부 검증
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BaseException(ErrorCode.INVALID_PASSWORD);
        }

        // 토큰 생성
        String accessToken = jwtUtil.createAccessToken(user);
        String refreshToken = jwtUtil.createRefreshToken(user);

        // redis에 저장
        refreshTokenRepository.save(user.getId(), refreshToken, refreshTokenExpiration);

        return LoginResponseDto.of(user, accessToken, refreshToken);
    }

    @Transactional
    public void logout(String accessToken, String refreshToken) {

        if (refreshToken == null) {
            throw new BaseException(ErrorCode.TOKEN_MISSING);
        }

        // refresh 토큰 유효성 검사
        log.info("토큰 유효성 검사 시작");
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new BaseException(ErrorCode.INVALID_TOKEN);
        }

        // 토큰 파싱해서 접두사 빼기
        String resolvedAccessToken = jwtUtil.resolveToken(accessToken);

        // access 토큰에서 가져온 userId 와 refresh 토큰에서 가져온 userId 가 일치하는지 검증
        Long userIdFromAccessToken = jwtUtil.getUserIdFromToken(resolvedAccessToken);
        Long userIdFromRefreshToken = jwtUtil.getUserIdFromToken(refreshToken);
        log.info("동일한 유저인지 검증 시작");
        if (!userIdFromAccessToken.equals(userIdFromRefreshToken)) {
            throw new BaseException(ErrorCode.TOKEN_USER_MISMATCH);
        }

        // access 토큰 redis에 블랙리스트 추가
        jwtUtil.addToBlacklistAccessToken(resolvedAccessToken);
        log.info("access 토큰을 블랙리스트에 추가");

        // refresh 토큰 redis에 블랙리스트 추가
        jwtUtil.addToBlacklistRefreshToken(refreshToken);
        log.info("refresh 토큰을 블랙리스트에 추가");
    }

    @Transactional
    public TokenResponseDto reissue(RefreshTokenRequestDto dto) {

        String refreshToken = dto.getRefreshToken();

        // 블랙리스트 확인
        String blacklistToken = stringRedisTemplate.opsForValue().get("blacklist:" + refreshToken);
        if (blacklistToken != null) {
            throw new BaseException(ErrorCode.IS_BLACKLISTED);
        }

        // 토큰 유효성 검사
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new BaseException(ErrorCode.INVALID_TOKEN);
        }

        // 새로운 access 토큰 발급
        Long userIdFromToken = jwtUtil.getUserIdFromToken(refreshToken);
        User user = userService.findByIdAndIsDeletedFalseOrElseThrow(userIdFromToken);
        String newAccessToken = jwtUtil.createAccessToken(user);


        return TokenResponseDto.of(newAccessToken);
    }
}
