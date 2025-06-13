package caffeine.nest_dev.domain.complaint.dto.request;

import caffeine.nest_dev.domain.complaint.entity.Answer;
import caffeine.nest_dev.domain.complaint.entity.Complaint;
import caffeine.nest_dev.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AnswerRequestDto {
    private Long id;
    private Long userId;
    private String contents;

    public Answer toEntity(User user, Complaint complaint){
        return Answer.builder()
                .user(user)
                .complaint(complaint)
                .contents(contents)
                .build();
    }
}
