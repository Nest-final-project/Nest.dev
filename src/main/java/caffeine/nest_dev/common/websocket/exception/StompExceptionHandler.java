package caffeine.nest_dev.common.websocket.exception;

import caffeine.nest_dev.common.websocket.dto.WebSocketErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompExceptionHandler extends StompSubProtocolErrorHandler {

    private final ObjectMapper objectMapper;

    /**
     * @param clientMessage 클라이언트에서 전송된 원본 STOMP 메시지
     * @param ex            발생한 예외 객체
     * @return 클라이언트에게 전송할 ERROR 프레임 메시지 (JSON 형태)
     */
    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {
        log.error("WebSocket 메시지 처리 중 오류 발생", ex);

        // 예외 원인 추출
        Throwable rootCause = getRootCause(ex);

        // 예외 타입별 처리
        if (rootCause instanceof BadCredentialsException) {
            return handleAuthenticationError(clientMessage, rootCause);
        } else if (rootCause instanceof AccessDeniedException) {
            return handleAuthorizationError(clientMessage, rootCause);
        } else {
            return handleGenericError(clientMessage, rootCause);
        }
    }

    @Override
    protected Message<byte[]> handleInternal(StompHeaderAccessor errorHeaderAccessor, byte[] errorPayload,
            Throwable cause, StompHeaderAccessor clientHeaderAccessor) {
        return super.handleInternal(errorHeaderAccessor, errorPayload, cause, clientHeaderAccessor);
    }

    /**
     * 인증 실패
     */
    private Message<byte[]> handleAuthenticationError(Message<byte[]> clientMessage, Throwable ex) {
        log.warn("WebSocket 인증 실패 : {}", ex.getMessage());

        String sessionId = extractSessionId(clientMessage);
        WebSocketErrorResponse webSocketErrorResponse = WebSocketErrorResponse.of("AUTHENTICATION_FAILED",
                ex.getMessage(),
                "401",
                sessionId);

        return createErrorMessage(webSocketErrorResponse);

    }

    /**
     * 인가 실패 처리
     */
    private Message<byte[]> handleAuthorizationError(Message<byte[]> clientMessage, Throwable ex) {
        log.warn("WebSocket 인가 실패: {}", ex.getMessage());

        String sessionId = extractSessionId(clientMessage);
        WebSocketErrorResponse webSocketErrorResponse = WebSocketErrorResponse.of("AUTHORIZATION_FAILED",
                ex.getMessage(),
                "403",
                sessionId);

        return createErrorMessage(webSocketErrorResponse);
    }

    /**
     * 일반적인 오류 처리
     */
    private Message<byte[]> handleGenericError(Message<byte[]> clientMessage, Throwable ex) {
        log.error("WebSocket 일반 오류: {}", ex.getMessage());

        String sessionId = extractSessionId(clientMessage);
        WebSocketErrorResponse webSocketErrorResponse = WebSocketErrorResponse.of("INTERNAl_ERROR",
                "서버 내부 오류 발생" + ex.getMessage(),
                "500",
                sessionId);

        return createErrorMessage(webSocketErrorResponse);
    }

    /**
     * ERROR 프레임 메시지 생성
     * <p>
     * {@link WebSocketErrorResponse} DTO -> STOMP ERROR 프레임으로 변환해 클라이언트에게 전송할 수 있는 형태로 만든다.
     * </p>
     *
     * @param errorResponse 에러 정보를 담은 DTO 객체
     * @return STOMP ERROR 프레임으로 변환된 메시지
     */
    private Message<byte[]> createErrorMessage(WebSocketErrorResponse errorResponse) {
        StompHeaderAccessor errorHeader = StompHeaderAccessor.create(StompCommand.ERROR);

        errorHeader.setMessage(errorResponse.getMessage());
        errorHeader.setNativeHeader("error-code", errorResponse.getError());
        errorHeader.setNativeHeader("http-status", errorResponse.getCode());
        errorHeader.setNativeHeader("timestamp", errorResponse.getTimestamp().toString());

        // 세션 정보 설정
        if (errorResponse.getSessionId() != null) {
            errorHeader.setSessionId(errorResponse.getSessionId());
        }

        String errorMsg = createJsonErrorMessage(errorResponse);
        return MessageBuilder.createMessage(
                errorMsg.getBytes(),
                errorHeader.getMessageHeaders()
        );
    }

    /**
     * 에러 응답 객체 -> JSON 변환 (직렬화 실패 시 기본 에러메시지 반환)
     *
     * @param errorResponse 변환할 에러 응답 객체
     * @return JSON 형태의 에러 메시지 문자열
     */
    private String createJsonErrorMessage(WebSocketErrorResponse errorResponse) {
        try {
            return objectMapper.writeValueAsString(errorResponse);
        } catch (Exception e) {
            log.error("JSON 에러 메시지 생성 실패", e);
            // 최후의 수단: 간단한 문자열 반환
            return "{\"error\":\"SERIALIZATION_ERROR\",\"message\":\"Failed to create error response\",\"code\":\"500\"}";
        }
    }

    /**
     * 클라이언트 메시지에서 세션 ID 추출
     *
     * @param clientMessage 세션 ID를 추출할 클라이언트 메시지
     * @return WebSocket 세션 ID 문자열(실패시 null 반환)
     */
    private String extractSessionId(Message<byte[]> clientMessage) {
        try {
            StompHeaderAccessor clientHeaders = StompHeaderAccessor.wrap(clientMessage);
            return clientHeaders.getSessionId();
        } catch (Exception e) {
            log.debug("세션 ID 추출 실패", e);
            return null;
        }
    }

    /**
     * 중첩된 예외에서 근본 원인 추출
     *
     * @param ex 원인을 찾을 예외 객체
     * @return 근본 원인 예외
     */
    private Throwable getRootCause(Throwable ex) {
        Throwable rootCause = ex;

        // MessageDeliveryException의 경우 원인 예외 추출
        if (ex instanceof MessageDeliveryException && ex.getCause() != null) {
            rootCause = ex.getCause();
        }

        // 중첩된 예외가 있는 경우 최종 원인까지 추적, 순환 참조 방지
        while (rootCause.getCause() != null && rootCause != rootCause.getCause()) {
            rootCause = rootCause.getCause();
        }

        return rootCause;
    }

}
