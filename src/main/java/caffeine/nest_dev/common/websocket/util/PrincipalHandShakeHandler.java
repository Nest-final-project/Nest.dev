package caffeine.nest_dev.common.websocket.util;

import com.sun.security.auth.UserPrincipal;
import java.security.Principal;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

@Slf4j
@Component
public class PrincipalHandShakeHandler extends DefaultHandshakeHandler {


    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler,
            Map<String, Object> attributes) {
        Object userId = attributes.get("userId");
        if (userId == null) {
            log.error("userId가 attributes에 없습니다. 인증 실패.");
            return null;
        }
        return new UserPrincipal(userId.toString());
    }
}
