package caffeine.nest_dev.domain.career.dto.request;

import caffeine.nest_dev.domain.career.entity.Career;
import caffeine.nest_dev.domain.profile.entity.Profile;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CareerRequestDto {

    @NotBlank(message = "회사명은 필수입니다.")
    private String company;

    @NotBlank(message = "시작시간은 필수입니다.")
    private LocalDateTime startAt;

    private LocalDateTime endAt;

    public static Career toEntity(CareerRequestDto dto, Profile profile) {
        return Career.builder()
                .company(dto.getCompany())
                .profile(profile)
                .startAt(dto.getStartAt())
                .endAt(dto.getEndAt())
                .build();
    }
}
