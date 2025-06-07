package caffeine.nest_dev.domain.keyword.dto.response;

import caffeine.nest_dev.domain.category.entity.Category;
import caffeine.nest_dev.domain.keyword.entity.Keyword;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class KeywordResponseDto {

    private Long id;
    private String name;

    public static KeywordResponseDto of(
            Keyword keyword) {
        return KeywordResponseDto.builder()
                .id(keyword.getId())
                .name(keyword.getName())
                .build();
    }
}