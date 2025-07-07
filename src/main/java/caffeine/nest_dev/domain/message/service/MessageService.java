package caffeine.nest_dev.domain.message.service;

import caffeine.nest_dev.common.websocket.redis.RedisPublisher;
import caffeine.nest_dev.domain.chatroom.entity.ChatRoom;
import caffeine.nest_dev.domain.chatroom.service.ChatRoomService;
import caffeine.nest_dev.domain.message.dto.request.MessageRequestDto;
import caffeine.nest_dev.domain.message.dto.response.MessageResponseDto;
import caffeine.nest_dev.domain.message.entity.Message;
import caffeine.nest_dev.domain.message.repository.MessageRepository;
import caffeine.nest_dev.domain.user.entity.User;
import caffeine.nest_dev.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserService userService;
    private final ChatRoomService chatRoomService;
    private final RedisPublisher redisPublisher;


    @Transactional
    public void sendMessage(Long chatRoomId, Long userId, MessageRequestDto requestDto) {
        User user = userService.findByIdAndIsDeletedFalseOrElseThrow(userId);

        ChatRoom chatRoom = chatRoomService.findByIdOrElseThrow(chatRoomId);

        // 송신자,수신자 구분
        boolean isMentorSender = chatRoom.getMentor().getId().equals(userId);
        User sender = isMentorSender ? chatRoom.getMentor() : chatRoom.getMentee();
        User receiver = isMentorSender ? chatRoom.getMentee() : chatRoom.getMentor();

        // 메시지 생성, 저장
        Message message = requestDto.toEntity(chatRoom, user);
        messageRepository.save(message);

        MessageResponseDto messageResponseDto = MessageResponseDto.of(message, sender, receiver, chatRoomId);

        redisPublisher.publish(
                new ChannelTopic("chat_room_" + chatRoomId),
                messageResponseDto
        );

    }
}
