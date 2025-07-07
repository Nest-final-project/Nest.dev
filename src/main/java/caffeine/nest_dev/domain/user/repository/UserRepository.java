package caffeine.nest_dev.domain.user.repository;

import caffeine.nest_dev.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailAndIsDeletedFalse(String email);

    Optional<User> findByEmail(String email);

    Optional<User> findByIdAndIsDeletedFalse(Long userId);

    boolean existsByEmail(String email);
}
