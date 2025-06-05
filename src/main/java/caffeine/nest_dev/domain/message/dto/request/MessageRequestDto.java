package caffeine.nest_dev.domain.message.dto.request;

import caffeine.nest_dev.domain.chatroom.entity.ChatRoom;
import caffeine.nest_dev.domain.message.entity.Message;
import caffeine.nest_dev.domain.reservation.entity.Reservation;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MessageRequestDto {

    private Long roomId;
    private Long reservationId;
    private Long senderId;
    private Long receiverId;
    private String content;

    public Message toEntity(ChatRoom chatRoom, Reservation reservation) {
        return Message.builder()
                .chatRoom(chatRoom)
                .reservation(reservation)
                .mentor(chatRoom.getMentor())
                .mentee(chatRoom.getMentee())
                .content(this.getContent())
                .build();
    }
}
