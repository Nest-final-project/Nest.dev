package caffeine.nest_dev.domain.career.repository;

import caffeine.nest_dev.domain.career.entity.Career;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CareerRepository extends JpaRepository<Career, Long> {

    @Query("select c from Career c where c.careerStatus = 'UNAUTHORIZED'")
    Page<Career> findByCareerStatus(Pageable pageable);

    Optional<Career> findByIdAndProfileId(Long careerId, Long profileId);

    Page<Career> findAllByProfileId(Long profileId, Pageable pageable);
}
