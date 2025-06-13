package caffeine.nest_dev.common.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;

@Getter
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@Builder
public class SliceResponse<T> {

    private final List<T> content;
    private final int currentPage;
    private final int size;
    private final boolean first;
    private final boolean last;
    private final boolean hasNext;
    private final boolean hasPrevious;

    public static <T> SliceResponse<T> of(Slice<T> slice) {
        return SliceResponse.<T>builder()
                .content(slice.getContent())
                .currentPage(slice.getNumber())
                .size(slice.getSize())
                .first(slice.isFirst())
                .last(slice.isLast())
                .hasNext(slice.hasNext())
                .hasPrevious(slice.hasPrevious())
                .build();
    }
}
