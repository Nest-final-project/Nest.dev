package caffeine.nest_dev.common.config;

import caffeine.nest_dev.common.websocket.util.StompPrincipal;
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
            String header = accessor.getFirstNativeHeader("Authorization");

            if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
                //Bearer 접두사 제거
                String jwt = header.substring(7);
                log.info("CONNECT JWT: {}", jwt);
                try {
                    Long userId = jwtUtil.getUserIdFromToken(jwt);

                    // 인증 객체 생성
//                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
//                            userId.toString(), null, List.of());

                    // session에 인증 정보 저장
                    accessor.setUser(new StompPrincipal(userId.toString()));
                    log.info("✅ Principal 설정 완료: {}", userId);
                    Principal principal = accessor.getUser();
                    log.info("✅ Principal set: {}", principal.getName()); // 이게 안 찍히면 실패

                    // session에 userId 저장
                    accessor.getSessionAttributes().put("userId", userId.toString());
                    log.info("✅ userId 저장 in sessionAttributes: {}", accessor.getSessionAttributes());

                    log.info("WebSocket CONNECT 성공 : User ID = {}", userId);
                } catch (Exception e) {
                    log.error("WebSocket JWT 인증 실패 : {}", e.getMessage());
                    throw new BadCredentialsException("JWT 인증에 실패했습니다.");
                }

            } else {
                throw new BadCredentialsException("JWT 토큰이 존재하지 않습니다.");
            }
        }
        return message;
    }
}
