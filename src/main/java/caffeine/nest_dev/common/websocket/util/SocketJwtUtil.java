package caffeine.nest_dev.common.websocket.util;

import caffeine.nest_dev.domain.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SocketJwtUtil {

    // 만료시간 30초 설정
    private final long tokenExpiration;
    private final SecretKey key;

    public SocketJwtUtil(@Value("${SECRET_KEY}") String secret,
            @Value("${jwt.socket-token-expiration}") long tokenExpiration) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.tokenExpiration = tokenExpiration;
    }

    // 소켓 전용 토큰 생성
    public String createSocketToken(User user) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .issuedAt(new Date(now))
                .expiration(new Date(now + tokenExpiration))
                .claim("type", "socket")
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Long getUserIdFromToken(String token) {
        JwtParser parser = Jwts.parser().verifyWith(key).build();
        Jws<Claims> claimsJws = parser.parseSignedClaims(token);
        String subject = claimsJws.getPayload().getSubject();
        return Long.parseLong(subject);

    }

    // 토큰 유효성 검사
    public boolean validateSocketToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            log.error("토큰이 비어있습니다.");
            return false;
        }

        try {
            JwtParser parser = Jwts.parser().verifyWith(key).build();
            Jws<Claims> claimsJws = parser.parseSignedClaims(token);

            String type = claimsJws.getPayload().get("type", String.class);
            if (!"socket".equals(type)) {
                log.error("소켓 전용 토큰이 아닙니다.");
                return false;
            }

            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.warn("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.warn("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.warn("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.warn("JWT 토큰이 비어있습니다.");
        }
        return false;
    }


}