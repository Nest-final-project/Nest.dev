package caffeine.nest_dev.domain.review.dto.response;

import caffeine.nest_dev.domain.review.entity.Review;
import caffeine.nest_dev.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ReviewResponseDto {

    private  Long id;

    private  Long reservationId;

    private  User mentor;

    private  User mentee;

    private  String content;


    public static ReviewResponseDto of(Review review){
        return ReviewResponseDto.builder()
                .id(review.getId())
                .reservationId(review.getReservation().getId())
                .mentor(review.getMentor())
                .mentee(review.getMentee())
                .content(review.getContent())
                .build();
    }

}
