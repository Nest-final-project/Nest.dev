package caffeine.nest_dev.domain.message.dto.request;

import caffeine.nest_dev.domain.chatroom.entity.ChatRoom;
import caffeine.nest_dev.domain.message.entity.Message;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MessageRequestDto {

    private String content;

    public Message toEntity(ChatRoom chatRoom) {
        return Message.builder()
                .reservation(chatRoom.getReservation())
                .mentor(chatRoom.getMentor())
                .mentee(chatRoom.getMentee())
                .content(this.getContent())
                .build();
    }
}
