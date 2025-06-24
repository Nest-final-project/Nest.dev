package caffeine.nest_dev.common.websocket.config;

import caffeine.nest_dev.common.config.JwtUtil;
import caffeine.nest_dev.common.websocket.util.StompPrincipal;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * CONNECT 연결시 헤더에서 jwt 토큰을 검증하는 방식
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 99) // 다른 보안 필터보다 WebSocketInterceptor를 우선 처리하기 위해 설정
public class AuthenticationChannelInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        // STOMP가 다룰 수 있도록 감싸줌. 헤더 정보에 접근 가능
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        log.info("Interceptor triggered, command: {}", accessor.getCommand());

        // CONNECT 명령일 경우에만 인증 절차 수행
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            log.info("WebSocket CONNECT 요청 - 세션: {}", accessor.getSessionId());
            try {
                authenticateConnection(accessor);
                log.info("WebSocket 인증 성공 - 사용자 : {}", accessor.getUser().getName());
            } catch (BadCredentialsException e) {
                log.warn("WebSocket 인증 실패 - 세션 : {}, 이유 : {}", accessor.getSessionId(), e.getMessage());
                throw e;
            } catch (Exception e) {
                log.error("WebSocket 인증 처리 중 예상치 못한 오류 - 세션 : {}", accessor.getSessionId(), e);
                throw new BadCredentialsException("인증 처리중 서버 오류가 발생했습니다.");
            }
        }
        return message;
    }

    private void authenticateConnection(StompHeaderAccessor accessor) {
        String header = accessor.getFirstNativeHeader("Authorization");
        // 헤더 유효성 검증
        validateAuthorizationHeader(header);

        // jwt 추출
        String jwt = header.substring(7).trim();
        if (jwt.isEmpty()) {
            throw new BadCredentialsException("JWT 토큰이 비어있습니다.");
        }

        // jwt 토큰 검증, 사용자 정보 추출
        Long userId = validateAndExtractUserId(jwt);
        // principal 설정

        setPrincipalAndSession(accessor, userId);

    }

    /**
     * Authorization 헤더 유효성 검증
     */
    private void validateAuthorizationHeader(String authHeader) {
        if (!StringUtils.hasText(authHeader)) {
            throw new BadCredentialsException("Authorization 헤더가 없습니다.");
        }
        if (!authHeader.startsWith("Bearer ")) {
            throw new BadCredentialsException("Bearer 토큰 형식이 아닙니다.");
        }
        if (authHeader.length() <= 7) {
            throw new BadCredentialsException("토큰이 비어있습니다.");
        }
    }

    /**
     * JWT 토큰 검증 및 사용자 ID 추출
     */
    private Long validateAndExtractUserId(String jwt) {
        try {
            // 토큰 유효성 검증
            if (!jwtUtil.validateToken(jwt)) {
                throw new BadCredentialsException("유효하지 않은 JWT 토큰입니다.");
            }

            // 사용자 ID 추출
            return jwtUtil.getUserIdFromToken(jwt);

        } catch (ExpiredJwtException e) {
            log.warn("만료된 JWT 토큰으로 WebSocket 연결 시도");
            throw new BadCredentialsException("토큰이 만료되었습니다. 다시 로그인해 주세요.");

        } catch (MalformedJwtException e) {
            log.warn("잘못된 형식의 JWT 토큰으로 WebSocket 연결 시도");
            throw new BadCredentialsException("토큰 형식이 올바르지 않습니다.");

        } catch (SecurityException e) {
            log.warn("서명이 유효하지 않은 JWT 토큰으로 WebSocket 연결 시도");
            throw new BadCredentialsException("토큰 서명이 유효하지 않습니다.");

        } catch (IllegalArgumentException e) {
            log.warn("잘못된 JWT 토큰으로 WebSocket 연결 시도: {}", e.getMessage());
            throw new BadCredentialsException("토큰이 올바르지 않습니다.");

        } catch (Exception e) {
            log.error("JWT 토큰 검증 중 예상치 못한 오류", e);
            throw new BadCredentialsException("토큰 검증 중 오류가 발생했습니다.");
        }
    }

    /**
     * Principal 및 세션 정보 설정
     */
    private void setPrincipalAndSession(StompHeaderAccessor accessor, Long userId) {
        try {
            // Principal 설정
            StompPrincipal principal = new StompPrincipal(userId.toString());
            accessor.setUser(principal);

            // 세션에 사용자 정보 저장
            accessor.getSessionAttributes().put("userId", userId.toString());
            accessor.getSessionAttributes().put("authenticatedAt", System.currentTimeMillis());

            // 검증: Principal이 제대로 설정되었는지 확인
            Principal setPrincipal = accessor.getUser();
            if (setPrincipal == null || !userId.toString().equals(setPrincipal.getName())) {
                throw new BadCredentialsException("Principal 설정에 실패했습니다.");
            }

            log.debug("Principal 및 세션 정보 설정 완료 - 사용자: {}", userId);

        } catch (Exception e) {
            log.error("Principal 설정 중 오류 발생 - 사용자: {}", userId, e);
            throw new BadCredentialsException("인증 정보 설정 중 오류가 발생했습니다.");
        }
    }
}