package caffeine.nest_dev.domain.chatroom.service;

import caffeine.nest_dev.common.enums.ErrorCode;
import caffeine.nest_dev.common.exception.BaseException;
import caffeine.nest_dev.domain.chatroom.dto.request.CreateChatRoomRequestDto;
import caffeine.nest_dev.domain.chatroom.dto.response.ChatRoomReadDto;
import caffeine.nest_dev.domain.chatroom.dto.response.ChatRoomResponseDto;
import caffeine.nest_dev.domain.chatroom.dto.response.ChatRoomStatusResponseDto;
import caffeine.nest_dev.domain.chatroom.dto.response.MessageDto;
import caffeine.nest_dev.domain.chatroom.entity.ChatRoom;
import caffeine.nest_dev.domain.chatroom.repository.ChatRoomRepository;
import caffeine.nest_dev.domain.chatroom.scheduler.enums.ChatRoomType;
import caffeine.nest_dev.domain.chatroom.scheduler.util.SaveTerminationRoomEvent;
import caffeine.nest_dev.domain.notification.service.NotificationService;
import caffeine.nest_dev.domain.reservation.entity.Reservation;
import caffeine.nest_dev.domain.reservation.enums.ReservationStatus;
import caffeine.nest_dev.domain.reservation.repository.ReservationRepository;
import caffeine.nest_dev.domain.user.entity.User;
import caffeine.nest_dev.domain.user.service.UserService;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ReservationRepository reservationRepository;
    private final UserService userService;

    private final ApplicationEventPublisher eventPublisher;
    private final NotificationService notificationService;

    // 채팅방 생성
    @Transactional
    public ChatRoomResponseDto createChatRooms(CreateChatRoomRequestDto requestDto) {

        Reservation reservation = reservationRepository.findById(requestDto.getReservationId()).orElseThrow(
                () -> new BaseException(ErrorCode.RESERVATION_NOT_FOUND)
        );

        // 예약이 결제된 상태에만 채팅방 생성 가능
        if (!ReservationStatus.PAID.equals(reservation.getReservationStatus())) {
            throw new BaseException(ErrorCode.CHATROOM_NOT_CREATED);
        }

        // 채팅방이 이미 존재하는 경우 기존의 채팅방을 반환
        Optional<ChatRoom> existChatRoom = chatRoomRepository.findByReservationId(reservation.getId());
        if (existChatRoom.isPresent()) {
            return ChatRoomResponseDto.of(existChatRoom.get());
        }

        User mentor = reservation.getMentor();
        User mentee = reservation.getMentee();

        ChatRoom chatRoom = requestDto.toEntity(mentor, mentee, reservation);

        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        log.info("채팅방 : chatRoomId = {}", chatRoom.getId());

        try {
            notificationService.send(mentee.getId(), "채팅방이 생성되었습니다.", ChatRoomType.OPEN, savedChatRoom.getId(),
                    reservation.getId());
            notificationService.send(mentor.getId(), "채팅방이 생성되었습니다.", ChatRoomType.OPEN, savedChatRoom.getId(),
                    reservation.getId());
        } catch (Exception e) {
            log.warn("채팅방 생성 알림 발송 실패", e);
        }

        // 채팅방 자동 종료 작업 등록
        eventPublisher.publishEvent(SaveTerminationRoomEvent.from(reservation));

        return ChatRoomResponseDto.of(savedChatRoom);
    }

    // 채팅방 목록 조회
    @Transactional(readOnly = true)
    public Slice<ChatRoomReadDto> findAllChatRooms(Long userId,
            Long lastMessageId,
            LocalDateTime cursorTime,
            Pageable pageable
    ) {

        return chatRoomRepository.findAllByMentorIdOrMenteeId(userId, lastMessageId, cursorTime, pageable);
    }


    // 채팅 내역 조회
    @Transactional(readOnly = true)
    public Slice<MessageDto> findAllMessage(Long id,
            Long chatRoomId,
            Long lastMessageId,
            Pageable pageable
    ) {

        Long userId = userService.findByIdAndIsDeletedFalseOrElseThrow(id).getId();

        return chatRoomRepository.findAllMessagesByChatRoomId(
                chatRoomId,
                lastMessageId,
                userId,
                pageable);
    }

    public ChatRoom findByIdOrElseThrow(Long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId).orElseThrow(
                () -> new BaseException(ErrorCode.CHATROOM_NOT_FOUND));
    }

    // 채팅방 종료 상태 확인
    @Transactional(readOnly = true)
    public ChatRoomStatusResponseDto isClosed(Long id, Long chatRoomId) {
        userService.findByIdAndIsDeletedFalseOrElseThrow(id).getId();

        // 엔티티를 새로 조회하여 최신 상태 반영
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(
                () -> new BaseException(ErrorCode.CHATROOM_NOT_FOUND));

        return ChatRoomStatusResponseDto.from(chatRoom);
    }
}
