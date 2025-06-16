package caffeine.nest_dev.domain.message.service;

import caffeine.nest_dev.common.enums.ErrorCode;
import caffeine.nest_dev.common.exception.BaseException;
import caffeine.nest_dev.domain.chatroom.entity.ChatRoom;
import caffeine.nest_dev.domain.chatroom.repository.ChatRoomRepository;
import caffeine.nest_dev.domain.message.dto.request.MessageRequestDto;
import caffeine.nest_dev.domain.message.dto.response.MessageResponseDto;
import caffeine.nest_dev.domain.message.entity.Message;
import caffeine.nest_dev.domain.message.repository.MessageRepository;
import caffeine.nest_dev.domain.user.entity.User;
import caffeine.nest_dev.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;
    private final UserService userService;

    @Transactional
    public void sendMessage(Long chatRoomId, Long userId, MessageRequestDto requestDto) {

        User user = userService.findByIdAndIsDeletedFalseOrElseThrow(userId);

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(
                () -> new BaseException(ErrorCode.CHATROOM_NOT_FOUND)
        );

        boolean isMentorSender = chatRoom.getMentor().getId().equals(userId);
        User sender = isMentorSender ? chatRoom.getMentor() : chatRoom.getMentee();
        User receiver = isMentorSender ? chatRoom.getMentee() : chatRoom.getMentor();

        // 메시지 생성, 저장
        Message message = requestDto.toEntity(chatRoom, user);
        messageRepository.save(message);

        MessageResponseDto messageResponseDto = MessageResponseDto.of(message, sender, receiver);

        // 메시지 전송 (수신자, 구독 경로, 보낼 메시지)
        simpMessagingTemplate.convertAndSendToUser(
                String.valueOf(receiver.getId()),
                "/queue/message",
                messageResponseDto
        );

    }
}
