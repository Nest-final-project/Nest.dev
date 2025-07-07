package caffeine.nest_dev.domain.consultation.dto.response;

import caffeine.nest_dev.domain.consultation.entity.Consultation;
import java.time.DayOfWeek;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ConsultationResponseDto {

    private Long id;
    private Long mentorId;
    private DayOfWeek dayOfWeek;
    private LocalTime startAt;
    private LocalTime endAt;

    public static ConsultationResponseDto of(Consultation consultation) {
        return ConsultationResponseDto.builder()
                .id(consultation.getId())
                .mentorId(consultation.getMentor().getId())
                .dayOfWeek(consultation.getDayOfWeek())// 날짜
                .startAt(consultation.getStartAt())
                .endAt(consultation.getEndAt())
                .build();
    }
}
