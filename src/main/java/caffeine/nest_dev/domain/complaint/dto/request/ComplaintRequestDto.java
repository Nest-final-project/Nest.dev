package caffeine.nest_dev.domain.complaint.dto.request;

import caffeine.nest_dev.domain.complaint.entity.Complaint;
import caffeine.nest_dev.domain.complaint.enums.ComplaintStatus;
import caffeine.nest_dev.domain.complaint.enums.ComplaintType;
import caffeine.nest_dev.domain.reservation.entity.Reservation;
import caffeine.nest_dev.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ComplaintRequestDto {
    private Long reservationId;
    private String title;
    private String contents;
    private ComplaintStatus status;
    private ComplaintType type;

    public Complaint toEntity(User user, Reservation reservation){
        return Complaint.builder()
                .user(user)
                .reservation(reservation)
                .title(title)
                .contents(contents)
                .complaintStatus(ComplaintStatus.PENDING)
                .complaintType(type)
                .build();

    }
}
