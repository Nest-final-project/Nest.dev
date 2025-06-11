package caffeine.nest_dev.domain.chatroom.scheduler.repository;

import caffeine.nest_dev.domain.chatroom.scheduler.entity.ChatRoomSchedule;
import caffeine.nest_dev.domain.chatroom.scheduler.enums.ScheduleStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomScheduleRepository extends JpaRepository<ChatRoomSchedule, Long> {

    List<ChatRoomSchedule> findAllByScheduleStatus(ScheduleStatus scheduleStatus);
}
