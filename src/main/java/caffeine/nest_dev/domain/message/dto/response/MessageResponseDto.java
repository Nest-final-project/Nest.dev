package caffeine.nest_dev.domain.message.dto.response;

import caffeine.nest_dev.domain.message.entity.Message;
import caffeine.nest_dev.domain.user.entity.User;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MessageResponseDto {

    private Long senderId;
    private Long receiverId;
    private String content;
    private LocalDateTime sentAt;

    public static MessageResponseDto of(Message message, User sender, User receiver) {
        return MessageResponseDto.builder()
                .senderId(sender.getId())
                .receiverId(receiver.getId())
                .content(message.getContent())
                .sentAt(message.getCreatedAt())
                .build();
    }
}
