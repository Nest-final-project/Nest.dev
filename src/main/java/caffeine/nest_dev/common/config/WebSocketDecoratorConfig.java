package caffeine.nest_dev.common.config;

import caffeine.nest_dev.common.websocket.util.WebSocketSessionRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;

/**
 * WebSocket 연결/종료 시점에 사용자 세션을 추적하기 위한 핸들러를 등록함
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebSocketDecoratorConfig {

    // 사용자 Id와 WebSocketSession을 매핑해 관리하는 세션 저장소
    private final WebSocketSessionRegistry sessionRegistry;

    /**
     * WebSocketHandlerDecoratorFactory Bean 등록 WebSocket 메시지 처리 흐름에 커스텀 Decorator 삽입 이를 통해 연결된 사용자 세션을 추적하고 종료할 때 제거할 수
     * 있음 userId마다 개별 Session을 가지고있음 -> 연결 종료시 두 명의 사용자의 세션 모두 종료시켜야 함
     *
     * @return 커스텀 WebSocketHandlerDecoratorFactory
     */
    @Bean
    public WebSocketHandlerDecoratorFactory webSocketHandlerDecoratorFactory() {
        return handler -> new WebSocketHandlerDecorator(handler) {

            @Override
            public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                String userId = session.getPrincipal().getName();
                log.info("afterConnectionEstablished : userId = {}", userId);
                sessionRegistry.register(userId, session);
                super.afterConnectionEstablished(session);
            }

            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
                String userId = session.getPrincipal().getName();
                log.info("afterConnectionClosed : userId = {}", userId);
                sessionRegistry.sessionClose(userId);
                super.afterConnectionClosed(session, closeStatus);
            }
        };
    }
}
