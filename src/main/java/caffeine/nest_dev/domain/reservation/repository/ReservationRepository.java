package caffeine.nest_dev.domain.reservation.repository;

import caffeine.nest_dev.domain.reservation.entity.Reservation;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findById(Long id);
}
