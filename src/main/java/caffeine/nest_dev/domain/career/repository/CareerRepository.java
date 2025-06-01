package caffeine.nest_dev.domain.career.repository;

import caffeine.nest_dev.domain.career.entity.Career;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface CareerRepository extends JpaRepository<Career, Long> {
}
