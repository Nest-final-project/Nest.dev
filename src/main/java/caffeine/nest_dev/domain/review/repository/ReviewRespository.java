package caffeine.nest_dev.domain.review.repository;

import caffeine.nest_dev.common.dto.PagingResponse;
import caffeine.nest_dev.domain.reservation.entity.Reservation;
import caffeine.nest_dev.domain.review.entity.Review;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRespository extends JpaRepository<Review, Long> {
    Optional<Review> findByReservationId(Long resservationId);

    Page<Review> findByMentorId(Long mentorId, Pageable pageable);

    Page<Review> findByMenteeId(Long menteeId, Pageable pageable);


}
