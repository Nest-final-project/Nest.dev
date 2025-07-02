package caffeine.nest_dev.domain.profile.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileImageResponseDto {

    private Long userId;
    private String imgUrl;

    public static ProfileImageResponseDto of(Long userId, String imgUrl) {
        return ProfileImageResponseDto.builder()
                .imgUrl(imgUrl)
                .userId(userId)
                .build();
    }
}
