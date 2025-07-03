package caffeine.nest_dev.domain.notification.dto.response;

import caffeine.nest_dev.domain.notification.entity.Notification;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class NotificationResponseDto {

    private Long id;
    private Long receiverId;
    private Long chatRoomId;
    private String content;
    private LocalDateTime createdAt;

    public static NotificationResponseDto from(Notification notification) {
        return NotificationResponseDto.builder()
                .id(notification.getId())
                .receiverId(notification.getReceiverId())
                .chatRoomId(notification.getChatRoomId())
                .content(notification.getContent())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
