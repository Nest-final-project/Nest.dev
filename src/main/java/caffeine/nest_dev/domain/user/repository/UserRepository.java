package caffeine.nest_dev.domain.user.repository;

import caffeine.nest_dev.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
