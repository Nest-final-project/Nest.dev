package caffeine.nest_dev.domain.profile.dto.response;

import caffeine.nest_dev.domain.keyword.dto.response.KeywordResponseDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RecommendedProfileResponseDto {

    private Long profileId;
    private Long userId;
    private String userName;
    private String profileTitle;
    private String categoryName;
    private List<KeywordResponseDto> keywords;
    private String imgUrl;

}
