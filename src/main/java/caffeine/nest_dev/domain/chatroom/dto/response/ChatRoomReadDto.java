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
    
    // 마지막 메시지 정보 추가
    private String lastMessageContent;
    private String lastMessageTime;
    private Long lastMessageSenderId;

    public static ChatRoomReadDto of(ChatRoom chatRoom) {
        User mentor = chatRoom.getMentor();
        User mentee = chatRoom.getMentee();
        return ChatRoomReadDto.builder()
                .roomId(chatRoom.getId())
                .mentorId(mentor.getId())
                .mentorName(mentor.getName())
                .menteeId(mentee.getId())
                .menteeName(mentee.getName())
                // 마지막 메시지는 쿼리에서 설정
                .build();
    }
    
    // 마지막 메시지 정보를 설정하는 생성자
    public static ChatRoomReadDto of(ChatRoom chatRoom, String lastMessageContent, 
                                   String lastMessageTime, Long lastMessageSenderId) {
        User mentor = chatRoom.getMentor();
        User mentee = chatRoom.getMentee();
        return ChatRoomReadDto.builder()
                .roomId(chatRoom.getId())
                .mentorId(mentor.getId())
                .mentorName(mentor.getName())
                .menteeId(mentee.getId())
                .menteeName(mentee.getName())
                .lastMessageContent(lastMessageContent)
                .lastMessageTime(lastMessageTime)
                .lastMessageSenderId(lastMessageSenderId)
                .build();
    }
}


