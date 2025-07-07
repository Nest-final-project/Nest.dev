package caffeine.nest_dev.domain.category.dto.response;

import caffeine.nest_dev.domain.category.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor()
public class CategoryResponseDto {

    private Long id;
    private String name;

    public static CategoryResponseDto of(Category category) {
        return CategoryResponseDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}
