package caffeine.nest_dev.common.websocket.config;

import java.security.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketDisconnectListener implements ApplicationListener<SessionDisconnectEvent> {

    private final RedisTemplate<String, Object> chatRedisTemplate;

    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        Principal user = event.getUser();
        if (user != null) {
            chatRedisTemplate.delete("ws:session:" + user.getName());
            log.info("delete session for user : {}", user.getName());
        }
    }
}
