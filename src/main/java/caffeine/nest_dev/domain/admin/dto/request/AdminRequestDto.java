package caffeine.nest_dev.domain.admin.dto.request;

import caffeine.nest_dev.domain.career.enums.CareerStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminRequestDto {

    private CareerStatus status;
}