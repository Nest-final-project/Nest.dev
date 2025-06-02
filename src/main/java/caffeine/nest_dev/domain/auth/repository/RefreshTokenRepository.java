package caffeine.nest_dev.domain.auth.repository;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {

    private final StringRedisTemplate stringRedisTemplate;

    // key 구분용
    private static final String PREFIX = "RT";

    // 로그인 시 refreshToken 저장
    public void save(Long userId, String refreshToken, long expirationMillis) {
        stringRedisTemplate.opsForValue().set(PREFIX + userId, refreshToken, Duration.ofMillis(expirationMillis));
    }

    // 저장된 refreshToken 조회
    public String findByUserId(Long userId) {
        return stringRedisTemplate.opsForValue().get(PREFIX + userId);
    }

    // 로그아웃 시 refreshToken 삭제
    public void delete(Long userId) {
        stringRedisTemplate.delete(PREFIX + userId);
    }
}
