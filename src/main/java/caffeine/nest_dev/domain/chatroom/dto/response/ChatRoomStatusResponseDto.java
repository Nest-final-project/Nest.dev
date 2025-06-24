package caffeine.nest_dev.domain.chatroom.dto.response;

import caffeine.nest_dev.domain.chatroom.entity.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatRoomStatusResponseDto {

    private final boolean isClosed;

    public static ChatRoomStatusResponseDto from(ChatRoom chatRoom) {
        return new ChatRoomStatusResponseDto(chatRoom.isClosed());
    }
}
