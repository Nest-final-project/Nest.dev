package caffeine.nest_dev.common.websocket.config;

import caffeine.nest_dev.common.websocket.exception.StompExceptionHandler;
import caffeine.nest_dev.common.websocket.util.PrincipalHandShakeHandler;
import caffeine.nest_dev.common.websocket.util.WebSocketAuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;

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
    private final WebSocketHandlerDecoratorFactory decoratorFactory;
    private final StompExceptionHandler exceptionHandler;
    private final PrincipalHandShakeHandler handShakeHandler;
    private final WebSocketAuthInterceptor webSocketAuthInterceptor;

    /**
     * STOMP 엔드포인트 등록 해당 엔드포인트로 웹소켓 연결
     *
     * @param registry STOMP 엔드포인트 등록용 객체
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-nest") // handshake 를 위해(최초 연결 시) 연결하는 endpoint
                .setAllowedOriginPatterns("*")  // cors 설정 (허용할 origin 지정)
                .setHandshakeHandler(handShakeHandler)
                .addInterceptors(webSocketAuthInterceptor)
                .withSockJS();
        registry.setErrorHandler(exceptionHandler);
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.addDecoratorFactory(decoratorFactory);
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
        registry.setUserDestinationPrefix("/user");
    }


}
