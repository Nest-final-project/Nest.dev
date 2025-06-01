package caffeine.nest_dev.domain.review.repository;

import caffeine.nest_dev.domain.reservation.entity.Reservation;
import caffeine.nest_dev.domain.review.entity.Review;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRespository extends JpaRepository<Review, Long> {
    Optional<Review> findByReservationId(Long resservationId);
}
