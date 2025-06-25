package caffeine.nest_dev.domain.chatroom.scheduler.util;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class SaveSchedulerEvent {

    private final long reservationId;
    private final LocalDateTime endAt;

    public SaveSchedulerEvent(long reservationId, LocalDateTime endAt) {
        this.reservationId = reservationId;
        this.endAt = endAt;
    }
}
