package caffeine.nest_dev.domain.review.dto.response;

import caffeine.nest_dev.domain.review.entity.Review;
import caffeine.nest_dev.domain.review.enums.ReviewStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ReviewResponseDto {

    private  Long id;

    private  Long reservationId;

    private  Long mentor;

    private  Long mentee;

    private  String content;

    private ReviewStatus reviewStatus;

    private LocalDateTime createdAt;

    public static ReviewResponseDto of(Review review){
        return ReviewResponseDto.builder()
                .id(review.getId())
                .reservationId(review.getReservation().getId())
                .mentor(review.getMentor().getId())
                .mentee(review.getMentee().getId())
                .content(review.getContent())
                .createdAt(review.getCreatedAt())
                .reviewStatus(review.getReviewStatus())
                .build();
    }
}
