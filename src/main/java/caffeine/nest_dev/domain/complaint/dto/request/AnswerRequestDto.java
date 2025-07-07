package caffeine.nest_dev.domain.complaint.dto.request;

import caffeine.nest_dev.domain.complaint.entity.Answer;
import caffeine.nest_dev.domain.complaint.entity.Complaint;
import caffeine.nest_dev.domain.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AnswerRequestDto {

    private Long id;
    private Long userId;
    @NotBlank(message = "내용은 비워둘 수 없습니다.")
    private String contents;

    public Answer toEntity(User user, Complaint complaint){
        return Answer.builder()
                .user(user)
                .complaint(complaint)
                .contents(contents)
                .build();
    }
}
