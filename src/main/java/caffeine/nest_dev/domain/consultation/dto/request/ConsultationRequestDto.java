package caffeine.nest_dev.domain.consultation.dto.request;

import caffeine.nest_dev.domain.consultation.entity.Consultation;
import caffeine.nest_dev.domain.user.entity.User;
import java.time.DayOfWeek;
import java.time.LocalTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ConsultationRequestDto {

    private DayOfWeek dayOfWeek;
    private LocalTime startAt;
    private LocalTime endAt;

    public Consultation toEntity(User user) {
        return Consultation.builder()
                .mentor(user)
                .dayOfWeek(dayOfWeek)
                .startAt(startAt)
                .endAt(endAt)
                .build();
    }
}
