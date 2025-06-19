//package caffeine.nest_dev.domain.chatroom.controller;
//
//import caffeine.nest_dev.domain.chatroom.dto.request.ChatEventMessage;
//import java.security.Principal;
//import lombok.RequiredArgsConstructor;
//import org.springframework.messaging.handler.annotation.DestinationVariable;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Controller;
//
//@Controller
//@RequiredArgsConstructor
//public class ChatRoomEventController {
//
//    private final SimpMessagingTemplate messagingTemplate;
//    private final ChatRoomEventService eventService;
//
//    @MessageMapping("/chat_room/{chatRoom_Id}/join")
//    public void handleJoin(@DestinationVariable Long chatRoom_Id,
//            Principal principal, ChatEventMessage message) {
//
//    }
//}
