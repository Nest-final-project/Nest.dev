package caffeine.nest_dev.domain.keyword.dto.request;

import caffeine.nest_dev.domain.category.entity.Category;
import caffeine.nest_dev.domain.keyword.entity.Keyword;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class KeywordRequestDto {

    @NotBlank(message = "키워드명은 필수입니다.")
    private String name;

    public Keyword toEntity() {
        return Keyword.builder()
                .name(this.name)
                .build();
    }
}