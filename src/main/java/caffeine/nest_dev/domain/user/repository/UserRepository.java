package caffeine.nest_dev.domain.user.repository;

import caffeine.nest_dev.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    default User findByIdOrElseThrow(Long userId) {
        return findById(userId).orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));
    }

    boolean existsByEmail(String email);
}
