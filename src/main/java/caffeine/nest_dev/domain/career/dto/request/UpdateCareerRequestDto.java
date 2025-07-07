package caffeine.nest_dev.domain.career.dto.request;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCareerRequestDto {

    private String company;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
}
