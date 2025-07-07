package caffeine.nest_dev.domain.chatroom.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageDto {

    private Long messageId;
    private Long chatRoomId;
    private Long mentorId;
    private Long menteeId;
    private Long senderId;
    private String content;
    private LocalDateTime sentAt;

    private Boolean isMine; // 현재 로그인한 사용자인지 여부 -> 프론트에서 ui 정렬 시 편함
}
