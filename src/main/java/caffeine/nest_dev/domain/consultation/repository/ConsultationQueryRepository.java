package caffeine.nest_dev.domain.consultation.repository;

import java.time.DayOfWeek;
import java.time.LocalTime;

public interface ConsultationQueryRepository {
    boolean existsConsultation(Long userId, DayOfWeek dayOfWeek, LocalTime startAt, LocalTime endAt);
}
