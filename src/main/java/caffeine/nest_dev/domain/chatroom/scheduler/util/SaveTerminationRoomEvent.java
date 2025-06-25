package caffeine.nest_dev.domain.chatroom.scheduler.util;

import caffeine.nest_dev.domain.reservation.entity.Reservation;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SaveTerminationRoomEvent {

    private final long reservationId;
    private final LocalDateTime endAt;

    public static SaveTerminationRoomEvent from(Reservation reservation) {
        return SaveTerminationRoomEvent.builder()
                .reservationId(reservation.getId())
                .endAt(reservation.getReservationEndAt())
                .build();
    }
}
