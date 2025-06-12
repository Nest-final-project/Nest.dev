package caffeine.nest_dev.domain.complaint.repository;

import caffeine.nest_dev.domain.complaint.entity.Complaint;
import caffeine.nest_dev.domain.complaint.enums.ComplaintType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    boolean existsByReservationId(Long reservationId);


    Page<Complaint> findALLByComplaintTypeIn(List<ComplaintType> complaintType, Pageable pageable);

    Page<Complaint> findAllByUserId(Long userId, Pageable pageable);
}
