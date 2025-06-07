package caffeine.nest_dev.domain.certificate.repository;

import caffeine.nest_dev.domain.career.entity.Career;
import caffeine.nest_dev.domain.certificate.entity.Certificate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {

    List<Certificate> findByCareer(Career career);

    Optional<Certificate> findByIdAndCareerId(Long certificateId, Long careerId);
}
