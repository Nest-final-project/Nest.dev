package caffeine.nest_dev.domain.career.repository;

import aj.org.objectweb.asm.commons.Remapper;
import caffeine.nest_dev.domain.career.entity.Career;
import caffeine.nest_dev.domain.career.enums.CareerStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface CareerRepository extends JpaRepository<Career, Long> {

    Page<Career> findByCareerStatus(CareerStatus careerStatus, Pageable pageable);
}
