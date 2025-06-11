package caffeine.nest_dev.domain.reservation.dto.request;

import caffeine.nest_dev.domain.reservation.entity.Reservation;
import caffeine.nest_dev.domain.reservation.enums.ReservationStatus;
import caffeine.nest_dev.domain.user.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReservationRequestDto {

    private Long mentor;
    private Long mentee;
    private ReservationStatus reservationStatus;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reservationStartAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reservationEndAt;


    public Reservation toEntity(User mentor, User mentee) {
        return Reservation.builder()
                .mentor(mentor)
                .mentee(mentee)
                .reservationStatus(ReservationStatus.REQUESTED)
                .reservationStartAt(reservationStartAt)
                .reservationEndAt(reservationEndAt)
                .build();

    }
}
