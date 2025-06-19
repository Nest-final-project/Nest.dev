package caffeine.nest_dev.domain.chatroom.scheduler.entity;

import caffeine.nest_dev.domain.chatroom.scheduler.enums.ChatRoomType;
import caffeine.nest_dev.domain.chatroom.scheduler.enums.ScheduleStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 채팅방 예약 상태 저장을 위한 Entity
 *
 * <li>예약 상태:
 *   <ul>
 *     <li><code>PENDING</code> - 예약 등록</li>
 *     <li><code>COMPLETE</code> - 작업 완료</li>
 *   </ul>
 * </li>
 *  <li>작업 타입:
 *    <ul>
 *      <li><code>OPEN</code> - 채팅방 생성용</li>
 *      <li><code>CLOSE</code> - 채팅방 종료용</li>
 *    </ul>
 *  </li>
 */

@Entity
@Builder
@Getter
@Table(
        name = "chat_room_schedule",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_reservation_type_status",
                        columnNames = {"reservation_id", "chat_room_type", "schedule_status"}
                )
        }
)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long reservationId;

    // 생성 예정 시간
    private LocalDateTime scheduledTime;

    @Enumerated(EnumType.STRING)
    private ScheduleStatus scheduleStatus;

    @Enumerated(EnumType.STRING)
    private ChatRoomType chatRoomType;

    public void updateStatus() {
        this.scheduleStatus = ScheduleStatus.COMPLETE;
    }
}
