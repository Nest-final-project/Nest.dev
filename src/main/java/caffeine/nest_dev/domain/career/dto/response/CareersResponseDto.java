package caffeine.nest_dev.domain.career.dto.response;

import caffeine.nest_dev.domain.career.entity.Career;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CareersResponseDto {

    private Long id;
    private String company;
    private LocalDateTime startAt;
    private LocalDateTime endAt;

    public static CareersResponseDto of(Career career) {
        return CareersResponseDto.builder()
                .id(career.getId())
                .company(career.getCompany())
                .startAt(career.getStartAt())
                .endAt(career.getEndAt())
                .build();
    }
}
