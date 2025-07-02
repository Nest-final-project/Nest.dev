package caffeine.nest_dev.domain.notification.service;

import static caffeine.nest_dev.domain.notification.enums.NotificationEventType.CHAT_OPEN;
import static caffeine.nest_dev.domain.notification.enums.NotificationEventType.CHAT_TERMINATION;

import caffeine.nest_dev.common.dto.PagingResponse;
import caffeine.nest_dev.domain.chatroom.scheduler.enums.ChatRoomType;
import caffeine.nest_dev.domain.notification.dto.response.NotificationResponse;
import caffeine.nest_dev.domain.notification.dto.response.NotificationResponseDto;
import caffeine.nest_dev.domain.notification.entity.Notification;
import caffeine.nest_dev.domain.notification.enums.NotificationEventType;
import caffeine.nest_dev.domain.notification.repository.EmitterRepository;
import caffeine.nest_dev.domain.notification.repository.NotificationRepository;
import caffeine.nest_dev.domain.user.entity.User;
import caffeine.nest_dev.domain.user.service.UserService;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    // sse timeout 시간
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    private final EmitterRepository emitterRepository;
    private final NotificationRepository notificationRepository;
    private final UserService userService;

    /**
     * 새로운 SseEmitter 생성 메서드
     *
     * @param userId      로그인한 userId
     * @param lastEventId 마지막으로 발생한 eventId
     * @return 서버에서 클라이언트와 매핑되는 Sse 통신 객체
     */
    @Transactional(readOnly = true)
    public SseEmitter subscribe(Long userId, String lastEventId) {

        // 데이터의 유실 시점을 파악하기 위해 시간을 함께 저장함
        String id = userId + "_" + System.currentTimeMillis();

        SseEmitter emitter = emitterRepository.save(id, new SseEmitter(DEFAULT_TIMEOUT));

        // SseEmitter 에러가 발생했을 경우, emitter 삭제
        emitter.onCompletion(() -> emitterRepository.deleteById(id));
        emitter.onTimeout(() -> emitterRepository.deleteById(id));
        emitter.onError((ex) -> {
            log.warn("SSE 에러 발생. emitterId: {}, error: {}", id, ex.getMessage());
            emitterRepository.deleteById(id);
        });

        // ⭐ 연결 직후 더미 이벤트 보내기
        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("SSE 연결 완료")
                    .id(id));
        } catch (IOException e) {
            log.error("SSE 연결 실패. emitterId: {}, error: {}", id, e.getMessage());
            emitterRepository.deleteById(id);
            throw new RuntimeException("SSE 연결 실패", e);
        }

        // 유실된 데이터가 있다면 데이터를 찾아 다시 클라이언트에게 전송
        if (lastEventId != null && !lastEventId.isEmpty()) {
            Map<String, Notification> events = emitterRepository.findAllEventCacheByUserId(String.valueOf(userId));

            long lastId = Long.parseLong(lastEventId);
            events.entrySet().stream()
                    .filter(entry -> Long.parseLong(entry.getKey()) > lastId)
                    .forEach(entry -> {
                        NotificationResponse response = NotificationResponse.from(entry.getValue());
                        NotificationEventType eventType = ChatRoomType.CLOSE.equals(entry.getValue().getChatRoomType())
                                ? CHAT_TERMINATION
                                : CHAT_OPEN;
                        sendNotification(emitter, entry.getKey(), response, eventType.getEventName());
                    });
        }
        return emitter;
    }

    // 알림을 만들어 로그인한 사용자에게 데이터 전송
    @Transactional
    public void send(Long receiverId, String content, ChatRoomType chatRoomType, Long chatRoomId, Long reservationId) {
        // 수신자, 내용을 담아서 알림 객체 생성
        Notification notification = createNotification(receiverId, content, chatRoomType, chatRoomId, reservationId);
        notificationRepository.save(notification);

        String userId = String.valueOf(receiverId);

        // 로그인한 유저의 SseEmitter 가져오기
        Map<String, SseEmitter> sseEmitters = emitterRepository.findAllEmitterByUserId(userId);

        if (sseEmitters.isEmpty()) {
            log.debug("userId {}에 대한 활성 SSE 연결이 없습니다.", userId);
            return;
        }
        NotificationResponse notificationResponse = NotificationResponse.from(notification);
        NotificationEventType eventType = ChatRoomType.CLOSE.equals(notification.getChatRoomType())
                ? CHAT_TERMINATION
                : CHAT_OPEN;

        sseEmitters.forEach(
                (key, emitter) -> {
                    // 유실된 데이터를 처리하기 위해 데이터 캐시 저장
                    emitterRepository.saveEventCache(key, notification);
                    sendNotification(emitter, key, notificationResponse, eventType.getEventName());
                });
    }

    // Notification 객체 생성
    private Notification createNotification(Long receiverId, String content, ChatRoomType chatRoomType,
            Long chatRoomId, Long reservationId) {
        return Notification.builder()
                .receiverId(receiverId)
                .chatRoomId(chatRoomId)
                .reservationId(reservationId)
                .content(content)
                .chatRoomType(chatRoomType)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private void sendNotification(SseEmitter emitter, String id, Object data, String eventName) {
        try {
            emitter.send(SseEmitter.event()
                    .id(id)
                    .name(eventName)
                    .data(data));
        } catch (IOException e) {
            // 로그만 남기고 예외를 던지지 않음 (scheduled task에서 호출되므로)
            log.warn("SSE 연결이 끊어진 클라이언트에게 알림 전송 실패. emitterId: {}, error: {}", id, e.getMessage());
            emitterRepository.deleteById(id);
            // 런타임 예외를 던지지 않음으로써 scheduled task 실행 중단 방지
        }
    }

    // 알림 내역 조회
    @Transactional(readOnly = true)
    public PagingResponse<NotificationResponseDto> getNotifications(Long id, Pageable pageable) {
        User user = userService.findByIdAndIsDeletedFalseOrElseThrow(id);

        Page<Notification> notifications = notificationRepository.findAllByReceiverId(user.getId(), pageable);

        Page<NotificationResponseDto> responseDtos = notifications.map(NotificationResponseDto::from);

        return PagingResponse.from(responseDtos);
    }
}
