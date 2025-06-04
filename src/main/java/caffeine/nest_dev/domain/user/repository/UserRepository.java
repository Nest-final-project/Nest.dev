package caffeine.nest_dev.domain.user.repository;

import caffeine.nest_dev.common.enums.ErrorCode;
import caffeine.nest_dev.common.exception.BaseException;
import caffeine.nest_dev.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailAndIsDeletedFalse(String email);

    Optional<User> findByIdAndIsDeletedFalse(Long userId);

    default User findByIdOrElseThrow(Long userId) {
        return findById(userId).orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_USER));
    }

    boolean existsByEmail(String email);
}
