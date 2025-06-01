package caffeine.nest_dev.domain.chatroom.repository;

import caffeine.nest_dev.domain.chatroom.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ChatRepository extends JpaRepository<ChatRoom, Long> {
}
