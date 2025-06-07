package caffeine.nest_dev.domain.message.service;

import caffeine.nest_dev.common.dto.CommonResponse;
import caffeine.nest_dev.common.enums.ErrorCode;
import caffeine.nest_dev.common.exception.BaseException;
import caffeine.nest_dev.domain.chatroom.entity.ChatRoom;
import caffeine.nest_dev.domain.chatroom.repository.ChatRoomRepository;
import caffeine.nest_dev.domain.message.dto.request.MessageRequestDto;
import caffeine.nest_dev.domain.message.dto.response.MessageResponseDto;
import caffeine.nest_dev.domain.message.entity.Message;
import caffeine.nest_dev.domain.message.repository.MessageRepository;
import caffeine.nest_dev.domain.reservation.entity.Reservation;
import caffeine.nest_dev.domain.reservation.repository.ReservationRepository;
import caffeine.nest_dev.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatRoomRepository chatRoomRepository;
    private final ReservationRepository reservationRepository;
    private final MessageRepository messageRepository;

    // TODO :  인증 절차 도입 후 수정
    @Transactional
    public void sendMessage(Long chatRoomId, MessageRequestDto requestDto) {

        try {

            ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(
                    () -> new BaseException(ErrorCode.CHATROOM_NOT_FOUND)
            );
            Reservation reservation = reservationRepository.findById(requestDto.getReservationId()).orElseThrow(
                    () -> new BaseException(ErrorCode.RESERVATION_NOT_FOUND)
            );

            // sender / receiver 식별
            Long senderId = requestDto.getSenderId();

            boolean isMentorSender = chatRoom.getMentor().getId().equals(senderId);
            User sender = isMentorSender ? chatRoom.getMentor() : chatRoom.getMentee();
            User receiver = isMentorSender ? chatRoom.getMentee() : chatRoom.getMentor();

            // 메시지 생성, 저장
            Message message = requestDto.toEntity(chatRoom, reservation);
            messageRepository.save(message);

            MessageResponseDto messageResponseDto = MessageResponseDto.of(message, sender, receiver);

            // 메시지 전송 (구독 경로, 보낼 메시지)
            // 구독한 모든 클라이언트에게 브로드캐스트
            simpMessagingTemplate.convertAndSend(
                    "/topic/chat_room/" + chatRoomId,
                    messageResponseDto
            );
        } catch (BaseException e) {
            simpMessagingTemplate.convertAndSend(
                    "/queue/error/" + requestDto.getSenderId(),
                    CommonResponse.of(e.getErrorCode())
            );

        }

//        // 메시지 전송 (수신자, 구독 경로, 보낼 메시지)
//        simpMessagingTemplate.convertAndSendToUser(
//                String.valueOf(receiver.getId()),
//                "/queue/message",
//                messageResponseDto
//        );
    }
}
