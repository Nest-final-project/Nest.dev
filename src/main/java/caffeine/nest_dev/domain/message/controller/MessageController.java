package caffeine.nest_dev.domain.message.controller;

import caffeine.nest_dev.domain.message.dto.request.MessageRequestDto;
import caffeine.nest_dev.domain.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

@Slf4j
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
            Message<?> message,
            @Payload MessageRequestDto requestDto) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        log.info("🔥 SimpUser: {}", accessor.getUser());

        String userIdstr = (String) accessor.getSessionAttributes().get("userId");
        if (userIdstr == null) {
            log.error("❌ userId from sessionAttributes is null");
        } else {
            log.info("✅ userId from sessionAttributes = {}", userIdstr);
            long userId = Long.parseLong(userIdstr);
            messageService.sendMessage(chatRoomId, userId, requestDto);

//        }
//        if (principal == null) {
//            log.error("❌ accessor.getUser() is null too!");
//        } else {
//            log.info("✅ principal in accessor: {}", principal.getName());
//            long userId = Long.parseLong(principal.getName());
//            log.info("메시지 보내는 userId : {}", userId);
//            messageService.sendMessage(chatRoomId, userId, requestDto);
//        }
//        Long userId = Long.parseLong(principal.getName());

        }
    }
}