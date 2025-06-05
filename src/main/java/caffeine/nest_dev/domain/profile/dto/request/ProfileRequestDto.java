package caffeine.nest_dev.domain.profile.dto.request;

import caffeine.nest_dev.domain.profile.entity.Profile;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ProfileRequestDto {
    private String title;
    private String introduction;
    private String imageUrl;
    private Long keywordId;
    private Long categoryId;


}
