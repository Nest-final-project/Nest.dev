package caffeine.nest_dev.common.config;

import caffeine.nest_dev.common.websocket.util.ChatHandshakeHandler;
import caffeine.nest_dev.common.websocket.util.WebSocketAuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * 웹소켓 메시지 브로커 설정 클래스 STOMP 프로토콜을 기반으로 하는 웹 소켓 통신 설정
 */
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * 웹소켓 연결 시 인증을 위한 HandShake 인터셉터
     */
    private final WebSocketAuthInterceptor webSocketAuthInterceptor;
    private final ChatHandshakeHandler chatHandshakeHandler;

    /**
     * STOMP 엔드포인트 등록 해당 엔드포인트로 웹소켓 연결
     *
     * @param registry STOMP 엔드포인트 등록용 객체
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-nest") // handshake 를 위해(최초 연결 시) 연결하는 endpoint
                .addInterceptors(webSocketAuthInterceptor) // socket 연결 전 인증, 검증 로직 수행
                .setAllowedOriginPatterns("*")  // cors 설정 (허용할 origin 지정)
                .setHandshakeHandler(chatHandshakeHandler)
                .withSockJS();  // 웹소켓을 지원하지 않는 브라우저도 사용할 수 있는 대체 옵션 지정
    }

    /**
     * 서버와 클라이언트 간 메시지 송수신 경로 정의
     *
     * @param registry 메시지 브로커 등록 객체
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/queue"); // 구독 경로 (ex) /user/queue/message
        registry.setApplicationDestinationPrefixes("/app"); // 메시지를 보낼 경로
        registry.setUserDestinationPrefix("/user"); // 본인에게 오는 메시지만 받도록
    }


}
