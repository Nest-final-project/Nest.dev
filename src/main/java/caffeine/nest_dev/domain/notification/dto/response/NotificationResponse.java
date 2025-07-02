package caffeine.nest_dev.domain.notification.dto.response;

import caffeine.nest_dev.domain.notification.entity.Notification;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationResponse {

    private String content;
    private Long chatRoomId;
    private Long reservationId;
    private LocalDateTime createdAt;

    public static NotificationResponse from(Notification notification) {
        return NotificationResponse.builder()
                .content(notification.getContent())
                .chatRoomId(notification.getChatRoomId())
                .reservationId(notification.getReservationId())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
