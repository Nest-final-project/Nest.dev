package caffeine.nest_dev.domain.chatroom.scheduler.entity;

import caffeine.nest_dev.domain.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_Id", nullable = false)
    private User receiver;

    private boolean isSent;

    private LocalDateTime scheduledAt;
    private LocalDateTime sentAt;   /****
     * 알림이 전송되었음을 표시하고 실제 전송 시간을 현재 시각으로 기록합니다.
     */

    public void update() {
        this.isSent = true;
        this.sentAt = LocalDateTime.now();
    }

}
