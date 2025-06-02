package caffeine.nest_dev.domain.review.dto.request;

import caffeine.nest_dev.domain.review.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewRequestDto {

    private String content;

    public Review toEntity(){
        return Review.builder()
                .content(content)
                .build();
    }

}
