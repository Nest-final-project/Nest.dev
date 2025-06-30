package caffeine.nest_dev.domain.complaint.repository;

import caffeine.nest_dev.domain.complaint.entity.Answer;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    boolean existsByComplaintId(Long complaintId);

    Optional<Answer> findByComplaint_Id(Long complaintId);
}
