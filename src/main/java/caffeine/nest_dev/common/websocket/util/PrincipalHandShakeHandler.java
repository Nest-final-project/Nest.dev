//package caffeine.nest_dev.common.websocket.util;
//
//import java.security.Principal;
//import java.util.Map;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.server.ServerHttpRequest;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.WebSocketHandler;
//import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
//
//@Slf4j
//@Component
//public class PrincipalHandShakeHandler extends DefaultHandshakeHandler {
//
//
//    @Override
//    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler,
//            Map<String, Object> attributes) {
//        log.info("🔥 determineUser() called in HandshakeHandler");
//        log.info("🔥 attributes: {}", attributes);
//
//        Object userId = attributes.get("userId");
//        if (userId != null) {
//            log.info("🔥 Creating Principal with userId: {}", userId);
//            return new StompPrincipal(userId.toString());
//        }
//
//        log.warn("❗ No userId found in session attributes");
//        return null;
//    }
//}
