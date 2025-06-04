package caffeine.nest_dev.domain.review.dto.response;

import caffeine.nest_dev.domain.review.entity.Review;
import caffeine.nest_dev.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@Builder
@AllArgsConstructor
public class ReviewResponseDto {

    private  Long id;

    private  Long reservationId;

    private  Long mentor;

    private  Long mentee;

    private  String content;


    public static ReviewResponseDto of(Review review){
        return ReviewResponseDto.builder()
                .id(review.getId())
                .reservationId(review.getReservation().getId())
                .mentor(review.getMentor().getId())
                .mentee(review.getMentee().getId())
                .content(review.getContent())
                .build();
    }
}
