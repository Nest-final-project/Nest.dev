package caffeine.nest_dev.domain.notification.controller;

import caffeine.nest_dev.common.config.JwtUtil;
import caffeine.nest_dev.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sse")
public class NotificationController {

    private final NotificationService notificationService;
    private final JwtUtil jwtUtil;

    /**
     * 클라이언트가 알림에 대한 SSE(Server-Sent Events) 구독을 시작할 수 있도록 SSE 연결을 생성합니다.
     *
     * @param token JWT 토큰 문자열로, 사용자 식별에 사용됩니다.
     * @param lastEventId 마지막으로 수신한 이벤트의 ID로, 누락된 이벤트 복구에 사용됩니다. 제공되지 않으면 빈 문자열이 기본값입니다.
     * @return 알림 SSE 스트림을 위한 SseEmitter 인스턴스
     */
    @GetMapping(value = "/notifications/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@RequestParam("token") String token,
            @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        return notificationService.subscribe(userId, lastEventId);
    }
}
