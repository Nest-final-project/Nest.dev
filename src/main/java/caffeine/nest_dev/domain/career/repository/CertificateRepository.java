package caffeine.nest_dev.domain.career.repository;

import caffeine.nest_dev.domain.career.entity.Career;
import caffeine.nest_dev.domain.career.entity.Certificate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {

    List<Certificate> findByCareer(Career career);

}
