package caffeine.nest_dev.domain.career.dto.request;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateCareerRequestDto {

    private String company;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private List<String> certificate;
}
