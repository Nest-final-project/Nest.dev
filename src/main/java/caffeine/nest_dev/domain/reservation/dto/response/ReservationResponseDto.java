package caffeine.nest_dev.domain.reservation.dto.response;

import caffeine.nest_dev.domain.reservation.entity.Reservation;
import caffeine.nest_dev.domain.reservation.enums.ReservationStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ReservationResponseDto {

    private Long id;
    private Long mentor;
    private Long mentee;
    private ReservationStatus reservationStatus;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reservationStartAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reservationEndAt;

    public static ReservationResponseDto of(Reservation reservation){
        return ReservationResponseDto.builder()
                .id(reservation.getId())
                .mentor(reservation.getMentor().getId())
                .mentee(reservation.getMentee().getId())
                .reservationStatus(reservation.getReservationStatus())
                .reservationStartAt(reservation.getReservationStartAt())
                .reservationEndAt(reservation.getReservationEndAt())
                .build();
    }

}
