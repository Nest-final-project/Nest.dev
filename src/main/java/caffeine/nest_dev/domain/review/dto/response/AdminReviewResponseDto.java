package caffeine.nest_dev.domain.review.dto.response;

import caffeine.nest_dev.domain.review.entity.Review;
import caffeine.nest_dev.domain.review.enums.ReviewStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AdminReviewResponseDto {

    private  Long id;

    private  Long reservationId;

    private  Long mentor;

    private  Long mentee;

    private  String content;

    private ReviewStatus reviewStatus;

    public static AdminReviewResponseDto of(Review review){
        return AdminReviewResponseDto.builder()
                .id(review.getId())
                .reservationId(review.getReservation().getId())
                .mentor(review.getMentor().getId())
                .mentee(review.getMentee().getId())
                .content(review.getContent())
                .reviewStatus(review.getReviewStatus())
                .build();
    }
}
