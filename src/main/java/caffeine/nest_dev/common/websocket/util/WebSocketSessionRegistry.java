package caffeine.nest_dev.common.websocket.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Slf4j
@Component
public class WebSocketSessionRegistry {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    // 사용자별로 세션 저장
    public void register(String userId, WebSocketSession socketSession) {
        sessions.put(userId, socketSession);
    }

    public void sessionClose(String userId) {
        sessions.remove(userId);
        log.info("남은 session 여부 (userId: {}): {}", userId, sessions.containsKey(userId));

    }

    public WebSocketSession getSession(String userId) {
        log.info("session ID = {}", sessions.get(userId));
        log.info("user ID = {}", sessions.keySet());
        return sessions.get(userId);
    }
}
