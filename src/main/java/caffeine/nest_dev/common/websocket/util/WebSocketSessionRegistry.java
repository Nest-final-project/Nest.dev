package caffeine.nest_dev.common.websocket.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

@Slf4j
@Component
public class WebSocketSessionRegistry {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    // 사용자별로 세션 저장
    public void register(String userId, WebSocketSession socketSession) {
        WebSocketSession existingSession = sessions.get(userId);
        if (existingSession != null && existingSession.isOpen()) {
            try {
                existingSession.close(CloseStatus.NORMAL.withReason("새로운 연결로 인한 세션 종료"));
            } catch (Exception e) {
                log.warn("기존 세션 종료 실패 : {}", e.getMessage());
            }
        }
        sessions.put(userId, socketSession);
    }

    public void sessionClose(String userId) {
        sessions.remove(userId);
        log.info("남은 session 여부 (userId: {}): {}", userId, sessions.containsKey(userId));

    }
}
