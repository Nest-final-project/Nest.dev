package caffeine.nest_dev.domain.consultation.repository;

import java.time.LocalDateTime;

public interface ConsultationQueryRepository {
    boolean existsConsultation(Long userId, LocalDateTime startAt, LocalDateTime endAt);
}
