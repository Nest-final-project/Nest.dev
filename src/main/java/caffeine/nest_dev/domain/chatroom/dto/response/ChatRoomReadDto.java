package caffeine.nest_dev.domain.chatroom.dto.response;

import caffeine.nest_dev.domain.chatroom.entity.ChatRoom;
import caffeine.nest_dev.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomReadDto {

    private Long roomId;
    private Long mentorId;
    private String mentorName;
    private Long menteeId;
    private String menteeName;

    public static ChatRoomReadDto of(ChatRoom chatRoom) {
        User mentor = chatRoom.getMentor();
        User mentee = chatRoom.getMentee();
        return ChatRoomReadDto.builder()
                .roomId(chatRoom.getId())
                .mentorId(mentor.getId())
                .mentorName(mentor.getName())
                .menteeId(mentee.getId())
                .menteeName(mentee.getName())
                .build();
    }
}


