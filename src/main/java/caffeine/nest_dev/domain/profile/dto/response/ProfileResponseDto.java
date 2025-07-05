package caffeine.nest_dev.domain.profile.dto.response;

import caffeine.nest_dev.domain.keyword.dto.response.KeywordResponseDto;
import caffeine.nest_dev.domain.profile.entity.Profile;
import caffeine.nest_dev.domain.user.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ProfileResponseDto {

    private Long id;
    private Long userId;
    private String role;
    private String title;
    private String introduction;
    private String category;
    private List<KeywordResponseDto> keywords;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String name;
    private String imgUrl;

    public static ProfileResponseDto from(Profile profile, User user) {
        return ProfileResponseDto.builder()
                .id(profile.getId())
                .userId(user.getId())
                .role(user.getUserRole().name())
                .title(profile.getTitle())
                .introduction(profile.getIntroduction())
                .category(profile.getCategory().getName())
                .keywords(profile.getProfileKeywords().stream()
                        .map(pk -> KeywordResponseDto.of(pk.getKeyword()))
                        .collect(Collectors.toList()))
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .name(user.getName())
                .imgUrl(user.getImgUrl())
                .build();
    }
}
