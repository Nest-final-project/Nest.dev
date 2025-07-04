package caffeine.nest_dev.domain.message.controller;

import caffeine.nest_dev.domain.message.dto.request.MessageRequestDto;
import caffeine.nest_dev.domain.message.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Tag(name = "Message", description = "WebSocket 메시지 API")
@Slf4j
@Controller
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    /**
     * 클라이언트가 메시지를 보내는 경로 ex ) /app/chat_room/{chatRoomId}/message
     */
    @Operation(summary = "채팅 메시지 전송", description = "WebSocket을 통해 채팅방에 메시지를 전송합니다")
    @MessageMapping("/chat_room/{chatRoomId}/message")
    public void chat(
            @Parameter(description = "채팅방 ID") @DestinationVariable Long chatRoomId,
            @Parameter(description = "인증된 사용자 정보") Principal principal,
            @Parameter(description = "메시지 내용") @Payload MessageRequestDto requestDto) {
        String principalName = principal.getName();
        long userId = Long.parseLong(principalName);

        log.info("✅ principal.getName() = {}", principalName);

        messageService.sendMessage(chatRoomId, userId, requestDto);


    }
}
