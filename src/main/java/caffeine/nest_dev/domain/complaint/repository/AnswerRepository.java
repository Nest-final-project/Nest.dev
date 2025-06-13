package caffeine.nest_dev.domain.complaint.repository;

import caffeine.nest_dev.domain.complaint.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    boolean existsByComplaintId(Long complaintId);
}
