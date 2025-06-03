package caffeine.nest_dev.domain.chatroom.dto.response;

import caffeine.nest_dev.domain.chatroom.entity.ChatRoom;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatRoomResponseDto {

    private Long roomId;
    private Long mentorId;
    private Long menteeId;

    private ChatRoomResponseDto(Long roomId, Long mentorId, Long menteeId) {
        this.roomId = roomId;
        this.mentorId = mentorId;
        this.menteeId = menteeId;
    }

    public static ChatRoomResponseDto of(ChatRoom chatRoom) {
        return ChatRoomResponseDto.builder()
                .roomId(chatRoom.getId())
                .mentorId(chatRoom.getMentor().getId())
                .menteeId(chatRoom.getMentee().getId())
                .build();
    }
}
