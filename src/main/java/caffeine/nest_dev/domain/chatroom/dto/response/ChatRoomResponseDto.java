package caffeine.nest_dev.domain.chatroom.dto.response;

import caffeine.nest_dev.domain.chatroom.entity.ChatRoom;
import lombok.Getter;

@Getter
public class ChatRoomResponseDto {

    private Long roomId;
    private Long mentorId;
    private Long menteeId;

    private ChatRoomResponseDto(Long roomId, Long mentorId, Long menteeId) {
        this.roomId = roomId;
        this.mentorId = mentorId;
        this.menteeId = menteeId;
    }

    public static ChatRoomResponseDto from(ChatRoom chatRoom) {
        return new ChatRoomResponseDto(
                chatRoom.getId(),
                chatRoom.getMentor().getId(),
                chatRoom.getMentee().getId()
        );
    }
}
