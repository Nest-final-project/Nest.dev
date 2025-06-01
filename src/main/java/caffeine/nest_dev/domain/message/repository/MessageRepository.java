package caffeine.nest_dev.domain.message.repository;

import caffeine.nest_dev.domain.message.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
