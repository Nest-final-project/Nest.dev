package caffeine.nest_dev.domain.profile.repository;

import caffeine.nest_dev.domain.profile.entity.Profile;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long>, ProfileRepositoryQuery {

    Page<Profile> findByUserIdAndIsDeletedFalse(Long userId, Pageable pageable);

    List<Profile> findByUserIdAndIsDeletedFalse(Long userId);

    Optional<Profile> findByIdAndUserId(Long profileId, Long id);

    boolean existsByUserIdAndCategoryIdAndIsDeletedFalse(Long userId, Long categoryId);
}
