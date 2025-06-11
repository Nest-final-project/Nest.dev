package caffeine.nest_dev.domain.consultation.repository;

import caffeine.nest_dev.domain.consultation.entity.Consultation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsultationRepository extends JpaRepository<Consultation, Long>, ConsultationQueryRepository{

    List<Consultation> findByMentorId(Long userId);

    Optional<Consultation> findByIdAndMentorId(Long consultationId, Long userId);
}
