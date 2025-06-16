package caffeine.nest_dev.domain.notification.service;

import caffeine.nest_dev.domain.notification.dto.response.NotificationResponse;
import caffeine.nest_dev.domain.notification.entity.Notification;
import caffeine.nest_dev.domain.notification.repository.EmitterRepository;
import caffeine.nest_dev.domain.user.entity.User;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
public class NotificationService {

    // sse timeout 시간
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    private final EmitterRepository emitterRepository;

    /**
     * 지정한 사용자에 대해 새로운 SseEmitter를 생성하고 구독을 시작합니다.
     *
     * 클라이언트가 마지막으로 수신한 이벤트 ID를 제공하면, 해당 ID 이후의 누락된 이벤트를 찾아 재전송합니다.
     *
     * @param userId      알림을 받을 사용자 ID
     * @param lastEventId 클라이언트가 마지막으로 수신한 이벤트 ID (누락된 이벤트 복구에 사용)
     * @return 클라이언트와의 SSE 연결을 위한 SseEmitter 객체
     */
    public SseEmitter subscribe(Long userId, String lastEventId) {

        // 데이터의 유실 시점을 파악하기 위해 시간을 함께 저장함
        String id = userId + "_" + System.currentTimeMillis();

        SseEmitter emitter = emitterRepository.save(id, new SseEmitter(DEFAULT_TIMEOUT));

        // SseEmitter 에러가 발생했을 경우, emitter 삭제
        emitter.onCompletion(() -> emitterRepository.deleteById(id));
        emitter.onTimeout(() -> emitterRepository.deleteById(id));

        // 유실된 데이터가 있다면 데이터를 찾아 다시 클라이언트에게 전송
        if (!lastEventId.isEmpty()) {
            Map<String, Object> events = emitterRepository.findAllEventCacheStartWithId(
                    String.valueOf(userId));
            events.entrySet().stream()
                    .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                    .forEach(entry -> sendToClient(emitter, entry.getKey(), entry.getValue()));
        }
        return emitter;
    }

    /**
     * 지정한 사용자에게 알림을 생성하고, 해당 사용자의 모든 활성화된 SSE 연결을 통해 실시간으로 알림을 전송합니다.
     *
     * @param receiver 알림을 받을 사용자
     * @param content 알림 내용
     */
    public void send(User receiver, String content) {
        Notification notification = createNotification(receiver, content);
        String userId = String.valueOf(receiver.getId());

        // 로그인한 유저의 SseEmitter 가져오기
        Map<String, SseEmitter> sseEmitter = emitterRepository.findAllStartWithId(userId);
        sseEmitter.forEach(
                (key, emitter) -> {
                    // 유실된 데이터를 처리하기 위해 데이터 캐시 저장
                    emitterRepository.saveEventCache(key, notification);
                    // 데이터 전송
                    sendToClient(emitter, key, NotificationResponse.from(notification));
                }
        );
    }

    /**
     * 지정된 수신자와 내용을 기반으로 새로운 Notification 객체를 생성합니다.
     *
     * @param receiver 알림을 받을 사용자
     * @param content 알림 내용
     * @return 생성된 Notification 객체
     */
    private Notification createNotification(User receiver, String content) {
        return Notification.builder()
                .receiver(receiver)
                .content(content)
                .build();
    }

    /**
     * 지정된 SseEmitter를 통해 클라이언트에게 이벤트 데이터를 전송합니다.
     *
     * @param emitter 이벤트를 전송할 SseEmitter 인스턴스
     * @param id 이벤트의 고유 식별자
     * @param data 전송할 데이터 객체
     * @throws RuntimeException 연결 오류가 발생한 경우 발생합니다.
     */
    private void sendToClient(SseEmitter emitter, String id, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(id)
                    .name("chat-termination")
                    .data(data));
        } catch (IOException e) {
            emitterRepository.deleteById(id);
            throw new RuntimeException("연결 오류");
        }
    }
}
