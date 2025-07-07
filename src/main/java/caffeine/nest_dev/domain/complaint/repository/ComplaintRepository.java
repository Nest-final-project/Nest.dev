package caffeine.nest_dev.domain.complaint.repository;

import caffeine.nest_dev.domain.complaint.entity.Complaint;
import caffeine.nest_dev.domain.complaint.enums.ComplaintType;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    boolean existsByReservationId(Long reservationId);


    Page<Complaint> findALLByComplaintTypeIn(List<ComplaintType> complaintType, Pageable pageable);

    Page<Complaint> findAllByUserId(Long userId, Pageable pageable);

    @Query("""
    SELECT c
    FROM Complaint c
    ORDER BY CASE WHEN c.complaintStatus = 'PENDING' THEN 0
        ELSE 1 END,
        c.createdAt DESC
    """)
    Page<Complaint> findSortedByStatusAndCreatedAt(Pageable pageable);
}
