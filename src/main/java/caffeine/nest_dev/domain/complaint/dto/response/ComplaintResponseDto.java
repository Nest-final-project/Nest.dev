package caffeine.nest_dev.domain.complaint.dto.response;

import caffeine.nest_dev.domain.complaint.entity.Complaint;
import caffeine.nest_dev.domain.complaint.enums.ComplaintStatus;
import caffeine.nest_dev.domain.complaint.enums.ComplaintType;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ComplaintResponseDto {

    private Long id;

    private Long userId;

    private Long reservationId;

    private String title;

    private String contents;

    private ComplaintStatus status;

    private ComplaintType type;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    public static ComplaintResponseDto of(Complaint complaint){
        return ComplaintResponseDto.builder()
                .id(complaint.getId())
                .userId(complaint.getUser().getId())
                .reservationId(complaint.getReservation() != null ? complaint.getReservation().getId() : null)
                .title(complaint.getTitle())
                .contents(complaint.getContents())
                .status(complaint.getComplaintStatus())
                .type(complaint.getComplaintType())
                .createdAt(complaint.getCreatedAt())
                .build();
    }

}
