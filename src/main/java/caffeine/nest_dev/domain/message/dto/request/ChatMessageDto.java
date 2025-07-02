package caffeine.nest_dev.domain.message.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ChatMessageDto {

    private final Long chatRoomId;
    private final Long senderId;
    private final String content;
}
