package caffeine.nest_dev.common.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@Builder
public class PagingResponse<T> {

    private final long totalElements;
    private final int totalPages;
    private final boolean hasNext;
    private final boolean hasPrevious;
    private final List<T> content;

    public static <T> PagingResponse<T> from(Page<T> page) {
        return PagingResponse.<T>builder()
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .content(page.getContent())
                .build();
    }
}
