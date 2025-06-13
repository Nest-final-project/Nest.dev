package caffeine.nest_dev.common.websocket.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class WebSocketSessionRegistry {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    // 사용자별로 세션 저장
    public void register(String userId, WebSocketSession socketSession) {
        sessions.put(userId, socketSession);
    }

    public void sessionClose(String userId) {
        sessions.remove(userId);
    }

    public WebSocketSession getSession(String userId) {
        return sessions.get(userId);
    }
}
