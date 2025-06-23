package caffeine.nest_dev.domain.notification.controller;

import caffeine.nest_dev.domain.notification.service.NotificationService;
import caffeine.nest_dev.domain.user.entity.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * SSE 를 통해 실시간 알림 구독 기능을 제공합니다.
 *
 * <p>
 * 클라이언트는 GET /sse/notifications/subscribe 요청을 보내 실시간 알림 스트림을 구독합니다. 인증된 사용자만 구독 가능하며, SSE 연결 시 마지막으로 수신한 이벤트 ID를
 * Last-Event-Id 헤더로 전달하면, 해당 ID 이후의 알림부터 전송합니다.
 * </p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/sse")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * SSE 연결 생성 클라이언트는 Authorization 헤더를 통해 인증 토큰을 제공해야 함
     *
     * @param userDetails 인증된 사용자 정보
     * @param lastEventId 클라이언트가 마지막으로 수신한 이벤트 id
     * @return SseEmitter 실시간 알림 스트림 객체
     */
    @GetMapping(value = "/notifications/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestHeader(value = "Last-Event-Id", required = false, defaultValue = "") String lastEventId) {
        Long userId = userDetails.getId();
        return notificationService.subscribe(userId, lastEventId);
    }
}
