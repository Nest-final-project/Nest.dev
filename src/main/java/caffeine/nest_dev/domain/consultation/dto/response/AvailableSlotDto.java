package caffeine.nest_dev.domain.consultation.dto.response;

import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AvailableSlotDto {

    private LocalTime availableStartAt;
    private LocalTime availableEndAt;

}
