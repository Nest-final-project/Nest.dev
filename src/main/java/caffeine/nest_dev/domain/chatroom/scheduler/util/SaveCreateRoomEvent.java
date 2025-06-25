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
public class SaveCreateRoomEvent {

    private final Long reservationId;
    private final LocalDateTime startAt;

    public static SaveCreateRoomEvent from(Reservation reservation) {
        return SaveCreateRoomEvent.builder()
                .reservationId(reservation.getId())
                .startAt(reservation.getReservationStartAt())
                .build();
    }
}
