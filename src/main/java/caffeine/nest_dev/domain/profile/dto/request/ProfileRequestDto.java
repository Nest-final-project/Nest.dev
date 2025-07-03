package caffeine.nest_dev.domain.profile.dto.request;

import caffeine.nest_dev.domain.category.entity.Category;
import caffeine.nest_dev.domain.keyword.entity.Keyword;
import caffeine.nest_dev.domain.keyword.entity.ProfileKeyword;
import caffeine.nest_dev.domain.profile.entity.Profile;
import caffeine.nest_dev.domain.user.entity.User;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class ProfileRequestDto {

    private String title;
    private String introduction;
    private List<Long> keywordId;
    private Long categoryId;
    private String accountNumber;

    public Profile toEntity(User user, Category category) {
        return Profile.builder()
                .user(user)
                .category(category)
                .title(title)
                .introduction(introduction)
                .profileKeywords(new ArrayList<>())
                .accountNumber(accountNumber)
                .build();
    }

    public List<ProfileKeyword> toProfileKeywords(Profile profile, List<Keyword> keywords) {
        return keywords.stream()
                .map(keyword -> ProfileKeyword.builder()
                        .profile(profile)
                        .keyword(keyword)
                        .build())
                .collect(Collectors.toList());
    }

}
