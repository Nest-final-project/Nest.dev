package caffeine.nest_dev.domain.message.controller;

import caffeine.nest_dev.domain.message.dto.request.MessageRequestDto;
import caffeine.nest_dev.domain.message.service.MessageService;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    /**
     * 클라이언트가 메시지를 보내는 경로 ex ) /app/chat_room/{chatRoomId}/message
     */
    @MessageMapping("/chat_room/{chatRoomId}/message")
    public void chat(
            @DestinationVariable Long chatRoomId,
            Principal principal,
            @Payload MessageRequestDto requestDto) {

        long userId = Long.parseLong(principal.getName());
        messageService.sendMessage(chatRoomId, userId, requestDto);
    }
}
