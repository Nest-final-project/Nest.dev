package caffeine.nest_dev.domain.review.repository;

import caffeine.nest_dev.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRespository extends JpaRepository<Review, Long> {
}
