package caffeine.nest_dev.domain.notification.dto.response;

import caffeine.nest_dev.domain.notification.entity.Notification;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class NotificationResponse {

    private String content;
    private LocalDateTime createdAt;

    /**
     * Notification 엔티티로부터 NotificationResponse 객체를 생성합니다.
     *
     * @param notification 변환할 Notification 엔티티
     * @return 주어진 Notification의 내용과 생성일시를 포함한 NotificationResponse 인스턴스
     */
    public static NotificationResponse from(Notification notification) {
        return NotificationResponse.builder()
                .content(notification.getContent())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
