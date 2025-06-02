package caffeine.nest_dev.domain.auth.service;

import caffeine.nest_dev.common.config.JwtUtil;
import caffeine.nest_dev.common.config.PasswordEncoder;
import caffeine.nest_dev.domain.auth.dto.request.AuthRequestDto;
import caffeine.nest_dev.domain.auth.dto.request.LoginRequestDto;
import caffeine.nest_dev.domain.auth.dto.response.AuthResponseDto;
import caffeine.nest_dev.domain.auth.dto.response.LoginResponseDto;
import caffeine.nest_dev.domain.auth.repository.RefreshTokenRepository;
import caffeine.nest_dev.domain.user.entity.User;
import caffeine.nest_dev.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
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
            throw new IllegalArgumentException("중복된 이메일 입니다.");
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
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        // 비밀번호 일치 여부 검증
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 토큰 생성
        String accessToken = jwtUtil.createAccessToken(user);
        String refreshToken = jwtUtil.createRefreshToken(user);

        // redis에 저장
        refreshTokenRepository.save(user.getId(), refreshToken, refreshTokenExpiration);

        return LoginResponseDto.of(user, accessToken, refreshToken);
    }

    @Transactional
    public void logout(Long userId) {
        String refreshToken = refreshTokenRepository.findByUserId(userId);
        if (refreshToken == null) {
            throw new IllegalArgumentException("저장된 토큰이 없습니다.");
        }

        // refresh 토큰 DB에서 삭제
        refreshTokenRepository.delete(userId);

        // refresh 토큰 redis에 블랙리스트 추가
        jwtUtil.addToBlacklistToken(refreshToken);
    }
}
