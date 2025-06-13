package caffeine.nest_dev.domain.chatroom.scheduler.entity;

import caffeine.nest_dev.domain.chatroom.scheduler.enums.ChatRoomType;
import caffeine.nest_dev.domain.chatroom.scheduler.enums.ScheduleStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 채팅방 예약 상태 저장을 위한 Entity
 */
@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 예약 id
    private Long reservationId;

    // 생성 예정 시간
    private LocalDateTime scheduledTime;

    // 채팅방 예약 상태
    @Enumerated(EnumType.STRING)
    private ScheduleStatus scheduleStatus;

    @Enumerated(EnumType.STRING)
    private ChatRoomType chatRoomType; // OPEN, CLOSE

    public void updateStatus() {
        this.scheduleStatus = ScheduleStatus.COMPLETE;
    }
}
