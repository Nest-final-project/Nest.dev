package caffeine.nest_dev.domain.message.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {

    private final Long chatRoomId;
    private final Long senderId;
    private final String content;
}
