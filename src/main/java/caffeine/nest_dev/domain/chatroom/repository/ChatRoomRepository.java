package caffeine.nest_dev.domain.chatroom.repository;

import caffeine.nest_dev.domain.chatroom.entity.ChatRoom;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long>, ChatRoomRepositoryQuery {

    Optional<ChatRoom> findByReservationId(Long reservationId);

    List<ChatRoom> findAllByMentorIdOrMenteeId(Long mentorId, Long menteeId);
}
