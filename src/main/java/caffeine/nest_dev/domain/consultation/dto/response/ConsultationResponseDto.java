package caffeine.nest_dev.domain.consultation.dto.response;

import caffeine.nest_dev.domain.consultation.entity.Consultation;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ConsultationResponseDto {

    private Long mentorId;
    private List<String> availableDays;
    private LocalDateTime startAt;
    private LocalDateTime endAt;

    public static ConsultationResponseDto of(Consultation consultation) {
        return ConsultationResponseDto.builder()
                .mentorId(consultation.getMentor().getId())
                .availableDays(List.of(consultation.getStartAt().getDayOfWeek().name())) // 날짜
                .startAt(consultation.getStartAt())
                .endAt(consultation.getEndAt())
                .build();
    }
}
