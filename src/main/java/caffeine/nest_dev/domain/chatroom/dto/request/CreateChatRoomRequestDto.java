package caffeine.nest_dev.domain.chatroom.dto.request;

import caffeine.nest_dev.domain.chatroom.entity.ChatRoom;
import caffeine.nest_dev.domain.reservation.entity.Reservation;
import caffeine.nest_dev.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateChatRoomRequestDto {

    private Long reservationId;

    public ChatRoom toEntity(User mentor, User mentee, Reservation reservation) {
        return ChatRoom.builder()
                .mentor(mentor)
                .mentee(mentee)
                .reservation(reservation)
                .isClosed(false)
                .build();
    }
}
