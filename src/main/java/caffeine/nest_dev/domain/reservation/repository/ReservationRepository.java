package caffeine.nest_dev.domain.reservation.repository;

import caffeine.nest_dev.domain.reservation.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Page<Reservation> findByMentorIdOrMenteeId(Long mentorId, Long menteeId, Pageable pageable);
}
