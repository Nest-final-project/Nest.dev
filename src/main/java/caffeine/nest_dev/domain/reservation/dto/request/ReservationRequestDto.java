package caffeine.nest_dev.domain.reservation.dto.request;

import caffeine.nest_dev.domain.reservation.entity.Reservation;
import caffeine.nest_dev.domain.reservation.enums.ReservationStatus;
import caffeine.nest_dev.domain.ticket.entity.Ticket;
import caffeine.nest_dev.domain.user.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReservationRequestDto {

    @NotNull(message="멘토 ID는 필수입니다.")
    private Long mentor;
    @NotNull(message="티켓 ID는 필수입니다.")
    private Long ticket;
    private ReservationStatus reservationStatus;
    @NotNull(message="예약 시작 시간은 필수입니다.")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime reservationStartAt;
    @NotNull(message="예약 종료 시간은 필수입니다.")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime reservationEndAt;


    public Reservation toEntity(User mentor, User mentee, Ticket ticket) {
        return Reservation.builder()
                .mentor(mentor)
                .mentee(mentee)
                .ticket(ticket)
                .reservationStatus(ReservationStatus.REQUESTED)
                .reservationStartAt(reservationStartAt)
                .reservationEndAt(reservationEndAt)
                .build();

    }
}
