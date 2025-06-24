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
import caffeine.nest_dev.domain.chatroom.scheduler.service.ChatRoomTerminationSchedulerService;
import caffeine.nest_dev.domain.reservation.entity.Reservation;
import caffeine.nest_dev.domain.reservation.repository.ReservationRepository;
import caffeine.nest_dev.domain.user.entity.User;
import caffeine.nest_dev.domain.user.service.UserService;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    // 채팅방 종료 예약 스케줄러
    private final ChatRoomTerminationSchedulerService schedulerService;

    // 채팅방 생성
    @Transactional
    public ChatRoomResponseDto createChatRooms(CreateChatRoomRequestDto requestDto) {

        // 예약이 유효한지 확인
        Reservation reservation = reservationRepository.findById(requestDto.getReservationId()).orElseThrow(
                () -> new BaseException(ErrorCode.RESERVATION_NOT_FOUND)
        );

        // 예약이 결제된 상태에만 채팅방 생성 가능
//        if (!ReservationStatus.PAID.equals(reservation.getReservationStatus())) {
//            throw new BaseException(ErrorCode.CHATROOM_NOT_CREATED);
//        }

        // 채팅방이 이미 존재하는 경우 기존의 채팅방을 반환
        Optional<ChatRoom> existChatRoom = chatRoomRepository.findByReservationId(reservation.getId());
        if (existChatRoom.isPresent()) {
            return ChatRoomResponseDto.of(existChatRoom.get());
        }

        // 멘토, 멘티 정보 추출
        User mentor = reservation.getMentor();
        User mentee = reservation.getMentee();
        log.info("mentorID = {}", mentor.getId());
        log.info("menteeID = {}", mentee.getId());

        ChatRoom chatRoom = requestDto.toEntity(mentor, mentee, reservation);

        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        log.info("채팅방 : chatRoomId = {}", chatRoom.getId());

        // 채팅방 자동 종료 작업 등록
        schedulerService.registerChatRoomCloseSchedule(reservation.getId(), reservation.getReservationEndAt());

        return ChatRoomResponseDto.of(savedChatRoom);
    }

    // 채팅방 목록 조회
    @Transactional(readOnly = true)
    public Slice<ChatRoomReadDto> findAllChatRooms(Long userId,
            Long lastMessageId,
            LocalDateTime cursorTime,
            Pageable pageable
    ) {

        Slice<ChatRoomReadDto> findChatRoomList = chatRoomRepository.findAllByMentorIdOrMenteeId(userId,
                lastMessageId,
                cursorTime,
                pageable);

        return findChatRoomList;
    }

    // 채팅 내역 조회
    @Transactional(readOnly = true)
    public Slice<MessageDto> findAllMessage(Long id,
            Long chatRoomId,
            Long lastMessageId,
            Pageable pageable
    ) {
        Long userId = userService.findByIdAndIsDeletedFalseOrElseThrow(id).getId();

        Slice<MessageDto> messageDtoList = chatRoomRepository.findAllMessagesByChatRoomId(
                chatRoomId,
                lastMessageId,
                pageable);

        // 자신이 보낸 메시지인지 판별
        for (MessageDto messageDto : messageDtoList.getContent()) {
            if (messageDto.getSenderId().equals(userId)) {
                messageDto.markAsMine();
            }
        }

        return messageDtoList;
    }

    public ChatRoom findByIdOrElseThrow(Long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId).orElseThrow(
                () -> new BaseException(ErrorCode.CHATROOM_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public ChatRoomStatusResponseDto isClosed(Long id, Long chatRoomId) {
        userService.findByIdAndIsDeletedFalseOrElseThrow(id).getId();

        // 엔티티를 새로 조회하여 최신 상태 반영
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(
                () -> new BaseException(ErrorCode.CHATROOM_NOT_FOUND));

        return ChatRoomStatusResponseDto.from(chatRoom);
    }
}
