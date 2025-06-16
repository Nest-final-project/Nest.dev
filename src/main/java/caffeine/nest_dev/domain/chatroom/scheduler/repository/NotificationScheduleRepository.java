package caffeine.nest_dev.domain.chatroom.scheduler.repository;

import caffeine.nest_dev.domain.chatroom.scheduler.entity.NotificationSchedule;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationScheduleRepository extends JpaRepository<NotificationSchedule, Long> {

    /**
 * 전달 여부에 따라 모든 NotificationSchedule 엔티티 목록을 조회합니다.
 *
 * @param b 알림이 이미 전송되었는지 여부(true: 전송됨, false: 미전송)
 * @return 조건에 맞는 NotificationSchedule 엔티티 리스트
 */
List<NotificationSchedule> findAllByIsSent(boolean b);
}
