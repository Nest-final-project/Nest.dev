package caffeine.nest_dev.domain.keyword.dto.response;

import caffeine.nest_dev.domain.keyword.entity.Keyword;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
