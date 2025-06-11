package caffeine.nest_dev.domain.consultation.dto.request;

import caffeine.nest_dev.domain.consultation.entity.Consultation;
import caffeine.nest_dev.domain.user.entity.User;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ConsultationRequestDto {

    private LocalDateTime startAt;
    private LocalDateTime endAt;

    public Consultation toEntity(User user) {
        return Consultation.builder()
                .mentor(user)
                .startAt(startAt)
                .endAt(endAt).build();
    }
}
