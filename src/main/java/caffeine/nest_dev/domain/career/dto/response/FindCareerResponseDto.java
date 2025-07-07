package caffeine.nest_dev.domain.career.dto.response;

import caffeine.nest_dev.domain.career.entity.Career;
import caffeine.nest_dev.domain.career.enums.CareerStatus;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class FindCareerResponseDto {

    private Long id;
    private String company;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private CareerStatus careerStatus;
    private List<CertificateResponseDto> certificates;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static FindCareerResponseDto of(
            Career career,
            List<CertificateResponseDto> certificateList
    ) {
        return FindCareerResponseDto.builder()
                .id(career.getId())
                .company(career.getCompany())
                .startAt(career.getStartAt())
                .endAt(career.getEndAt())
                .careerStatus(career.getCareerStatus())
                .certificates(certificateList)
                .createdAt(career.getCreatedAt())
                .updatedAt(career.getUpdatedAt())
                .build();
    }

}
