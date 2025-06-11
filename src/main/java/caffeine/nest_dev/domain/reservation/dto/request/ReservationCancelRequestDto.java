package caffeine.nest_dev.domain.reservation.dto.request;

import caffeine.nest_dev.domain.reservation.entity.Reservation;
import caffeine.nest_dev.domain.reservation.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReservationCancelRequestDto {

    private String cancellation;
    private ReservationStatus reservationStatus;


    public Reservation toEntity(){
        return Reservation.builder()
                .cancellation(cancellation)
                .reservationStatus(ReservationStatus.CANCELED)
                .build();
    }

}
