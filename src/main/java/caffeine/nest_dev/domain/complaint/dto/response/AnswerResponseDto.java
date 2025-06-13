package caffeine.nest_dev.domain.complaint.dto.response;

import caffeine.nest_dev.domain.complaint.entity.Answer;
import caffeine.nest_dev.domain.complaint.entity.Complaint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AnswerResponseDto {

    private Long id;
    private Long userId;
    private Long complaintId;
    private String contents;

    public static AnswerResponseDto of(Answer answer){
        return AnswerResponseDto.builder()
                .id(answer.getId())
                .userId(answer.getUser().getId())
                .complaintId(answer.getComplaint().getId())
                .contents(answer.getContents())
                .build();
    }

}
