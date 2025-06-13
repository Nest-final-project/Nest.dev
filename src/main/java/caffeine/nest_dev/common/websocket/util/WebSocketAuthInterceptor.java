package caffeine.nest_dev.common.websocket.util;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;


@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    private final SocketJwtUtil socketJwtUtil;

    /**
     * 핸드쉐이크 발생 전 호출해 HTTp 요청을 가로채 처리함. 요청받은 쿼리스트링에서 토큰을 추출 이후 socket 전용 token이 유효한지 검증한 뒤, userId를 추출해 세션에 저장하는 역할을 함
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
            Map<String, Object> attributes) {

        // 파라미터에서 token 추출
        String query = request.getURI().getQuery();
        if (query == null || !query.startsWith("token=")) {
            log.error("소켓 토큰이 없습니다.");
            return false;
        }

        String token = query.substring(6);

        // 토큰 유효성 검사
        if (!socketJwtUtil.validateSocketToken(token)) {
            return false;
        }

        // 토큰에서 userId를 추출해 저장 -> 소켓 세션에 저장
        Long userId = socketJwtUtil.getUserIdFromToken(token);
        attributes.put("userId", userId);
        log.info("소켓 인증 성공 userId: {}", userId);    // 잘 들어감 key:"userId" value : userId

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
            Exception exception) {

    }
}
