package caffeine.nest_dev.domain.notification.repository;

import caffeine.nest_dev.domain.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findAllByReceiverId(Long receiverId, Pageable pageable);
}
