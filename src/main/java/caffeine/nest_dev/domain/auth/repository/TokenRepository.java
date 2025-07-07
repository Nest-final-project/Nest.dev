package caffeine.nest_dev.domain.auth.repository;

import caffeine.nest_dev.oauth2.dto.response.OAuth2LoginResponseDto;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TokenRepository {

    private final StringRedisTemplate stringRedisTemplate;
    private final RedisTemplate<String, Object> objectRedisTemplate;

    // key 구분용
    private static final String PREFIX = "RT";
    private static final String SUFFIX = "SOCIAL";

    // 로그인 시 refreshToken 저장
    public void save(Long userId, String refreshToken, long expirationMillis) {
        stringRedisTemplate.opsForValue()
                .set(PREFIX + userId, refreshToken, Duration.ofMillis(expirationMillis));
    }

    // 저장된 refreshToken 조회
    public String findByUserId(Long userId) {
        return stringRedisTemplate.opsForValue().get(PREFIX + userId);
    }

    // 소셜 로그인 정보 저장
    public void saveSocial(Long userId, OAuth2LoginResponseDto dto, long expirationMillis) {
        objectRedisTemplate.opsForValue()
                .set(SUFFIX + userId, dto, Duration.ofMillis(expirationMillis));
    }

}
