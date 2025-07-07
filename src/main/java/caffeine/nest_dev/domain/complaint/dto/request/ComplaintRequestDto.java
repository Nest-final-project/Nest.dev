package caffeine.nest_dev.domain.complaint.dto.request;

import caffeine.nest_dev.domain.complaint.entity.Complaint;
import caffeine.nest_dev.domain.complaint.enums.ComplaintStatus;
import caffeine.nest_dev.domain.complaint.enums.ComplaintType;
import caffeine.nest_dev.domain.reservation.entity.Reservation;
import caffeine.nest_dev.domain.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ComplaintRequestDto {
    private Long reservationId;
    @NotBlank(message="제목은 비워둘 수 없습니다.")
    private String title;
    @NotBlank(message="내용은 비워둘 수 없습니다.")
    private String contents;
    private ComplaintStatus status;
    @NotNull(message = "문의 유형을 선택해주세요.")
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
