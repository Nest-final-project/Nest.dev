package caffeine.nest_dev.common.config;

import caffeine.nest_dev.domain.websocket.util.WebSocketAuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketAuthInterceptor webSocketAuthInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-nest") // handshake 를 위해 연결하는 endpoint
                .addInterceptors(webSocketAuthInterceptor) // socket 연결 전 설정
                .setAllowedOriginPatterns("*")  // cors 설정 (허용할 origin 지정)
                .withSockJS();  // 웹소켓을 지원하지 않는 브라우저도 사용할 수 있는 대체 옵션 지정
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/queue");    // 메시지 브로커 활성화, 구독 메시지 접두사 설정 -> 메시지 받을 때
        // 클라이언트에서 발생한 메시지 중 Destination이 해당 경로로 시작하는 메시지를 메시지 브로커에서 처리하도록 함
        registry.setApplicationDestinationPrefixes("/app"); // 메시지를 보낼 때
        registry.setUserDestinationPrefix("/user"); // 본인에게 오는 메시지만 받도록
    }


}
