package caffeine.nest_dev.domain.certificate.repository;

import caffeine.nest_dev.domain.certificate.entity.Certificate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {

    List<Certificate> findByCareerId(Long careerId);
}
