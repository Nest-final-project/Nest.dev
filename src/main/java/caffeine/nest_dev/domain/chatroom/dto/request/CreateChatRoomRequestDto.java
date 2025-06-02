package caffeine.nest_dev.domain.chatroom.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateChatRoomRequestDto {

    private Long reservationId;
    private Long mentorId;
    private Long menteeId;
}
