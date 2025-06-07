package caffeine.nest_dev.domain.category.dto.request;

import caffeine.nest_dev.domain.category.entity.Category;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class CategoryRequestDto {

    @NotBlank(message = "카테고리명은 필수입니다.")
    private String name;

    public Category toEntity() {
        return Category.builder()
                .name(this.name)
                .build();
    }
}
