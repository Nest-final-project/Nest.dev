package caffeine.nest_dev.domain.chatroom.scheduler.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class NotificationSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long chatRoomId;

    private Long reservationId;

    @Column(name = "user_id", nullable = false)
    private Long receiverId;

    private boolean isSent;

    private LocalDateTime scheduledAt;
    private LocalDateTime sentAt;   // 실제 전송된 시간

    public void markAsSent() {
        this.isSent = true;
        this.sentAt = LocalDateTime.now();
    }

}
