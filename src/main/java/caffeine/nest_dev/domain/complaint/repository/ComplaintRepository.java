package caffeine.nest_dev.domain.complaint.repository;

import caffeine.nest_dev.domain.complaint.entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    boolean existsByReservationId(Long reservationId);
}
