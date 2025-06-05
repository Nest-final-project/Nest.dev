package caffeine.nest_dev.domain.career.dto.request;

import caffeine.nest_dev.domain.career.entity.Career;
import caffeine.nest_dev.domain.career.enums.CareerStatus;
import caffeine.nest_dev.domain.profile.entity.Profile;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CareerRequestDto {

    private String company;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private List<String> certificates;

    public Career toEntity(CareerRequestDto dto, Profile profile) {
        return Career.builder()
                .company(dto.company)
                .profile(profile)
                .startAt(dto.startAt)
                .endAt(dto.endAt)
                .careerStatus(CareerStatus.UNAUTHORIZED)
                .build();
    }
}
