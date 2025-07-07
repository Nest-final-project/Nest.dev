package caffeine.nest_dev.domain.message.dto.request;

import caffeine.nest_dev.domain.chatroom.entity.ChatRoom;
import caffeine.nest_dev.domain.message.entity.Message;
import caffeine.nest_dev.domain.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MessageRequestDto {

    private String content;

    public Message toEntity(ChatRoom chatRoom, User user) {
        return Message.builder()
                .chatRoom(chatRoom)
                .reservation(chatRoom.getReservation())
                .mentor(chatRoom.getMentor())
                .mentee(chatRoom.getMentee())
                .sender(user)
                .content(this.getContent())
                .build();
    }
}
