package caffeine.nest_dev.domain.review.repository;

import caffeine.nest_dev.domain.review.entity.Review;
import caffeine.nest_dev.domain.review.enums.ReviewStatus;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByReservationId(Long reservationId);

    Page<Review> findByMentorIdAndReviewStatus(Long mentorId, Pageable pageable, ReviewStatus reviewStatus);

    Page<Review> findByMenteeIdAndReviewStatus(Long menteeId, Pageable pageable, ReviewStatus reviewStatus);

    Page<Review> findByMentorIdOrMenteeId(Long mentorId, Long menteeId, Pageable pageable);
}
