package caffeine.nest_dev.domain.reservation.repository;

import caffeine.nest_dev.domain.reservation.entity.Reservation;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Page<Reservation> findByMentorIdOrMenteeId(Long mentorId, Long menteeId, Pageable pageable);

    boolean existsByMentorIdOrMenteeIdAndReservationStartAtAndReservationEndAt(Long mentorId,
            Long menteeId, LocalDateTime startAt, LocalDateTime endAt);
}
