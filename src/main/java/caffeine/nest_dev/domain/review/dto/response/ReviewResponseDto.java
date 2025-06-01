package caffeine.nest_dev.domain.review.dto.response;

import caffeine.nest_dev.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewResponseDto {

    private final Long id;

    private final Long reservationId;

    private final User mentor;

    private final User mentee;

    private final String content;

}
