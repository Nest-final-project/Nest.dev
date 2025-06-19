package caffeine.nest_dev.common.config;

import java.util.List;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

        // CONNECT 명령일 경우에만 인증 절차 수행
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String header = accessor.getFirstNativeHeader("Authorization");

            if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
                //Bearer 접두사 제거
                String jwt = header.substring(7);

                try {
                    Long userId = jwtUtil.getUserIdFromToken(jwt);

                    // 인증 객체 생성
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userId, null, List.of());

                    // session에 인증 정보 저장
                    accessor.setUser(authentication);

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
