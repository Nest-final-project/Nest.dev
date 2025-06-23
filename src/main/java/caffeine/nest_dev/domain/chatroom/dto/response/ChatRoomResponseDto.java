package caffeine.nest_dev.domain.chatroom.dto.response;

import caffeine.nest_dev.domain.chatroom.entity.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomResponseDto {

    private Long roomId;
    private Long mentorId;
    private Long menteeId;

    public static ChatRoomResponseDto of(ChatRoom chatRoom) {
        return ChatRoomResponseDto.builder()
                .roomId(chatRoom.getId())
                .mentorId(chatRoom.getMentor().getId())
                .menteeId(chatRoom.getMentee().getId())
                .build();
    }
}
