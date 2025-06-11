package caffeine.nest_dev.domain.chatroom.service;

import caffeine.nest_dev.common.enums.ErrorCode;
import caffeine.nest_dev.common.exception.BaseException;
import caffeine.nest_dev.domain.chatroom.dto.request.CreateChatRoomRequestDto;
import caffeine.nest_dev.domain.chatroom.dto.response.ChatRoomResponseDto;
import caffeine.nest_dev.domain.chatroom.entity.ChatRoom;
import caffeine.nest_dev.domain.chatroom.repository.ChatRoomRepository;
import caffeine.nest_dev.domain.reservation.entity.Reservation;
import caffeine.nest_dev.domain.reservation.enums.ReservationStatus;
import caffeine.nest_dev.domain.reservation.repository.ReservationRepository;
import caffeine.nest_dev.domain.user.entity.User;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ReservationRepository reservationRepository;

    // 채팅방 생성
    @Transactional
    public ChatRoomResponseDto createChatRooms(CreateChatRoomRequestDto requestDto) {

        // 예약이 유효한지 확인
        Reservation reservation = reservationRepository.findById(requestDto.getReservationId()).orElseThrow(
                () -> new BaseException(ErrorCode.RESERVATION_NOT_FOUND)
        );

        // 예약이 결제된 상태에만 채팅방 생성 가능
        if (!ReservationStatus.PAID.equals(reservation.getReservationStatus())) {
            throw new BaseException(ErrorCode.CHATROOM_NOT_CREATED);
        }

//        if (!reservation.getMentor().getId().equals(userId) && !reservation.getMentee().getId().equals(userId)) {
//            throw new IllegalArgumentException("접근 권한이 없습니다.");
//        }

        // 채팅방이 이미 존재하는 경우 기존의 채팅방을 반환
        Optional<ChatRoom> existChatRoom = chatRoomRepository.findByReservationId(reservation.getId());
        if (existChatRoom.isPresent()) {
            return ChatRoomResponseDto.of(existChatRoom.get());
        }

        // 멘토, 멘티 정보 추출
        User mentor = reservation.getMentor();
        User mentee = reservation.getMentee();
        System.out.println("mentor Id" + mentor.getId());
        System.out.println("mentee Id" + mentee.getId());

        ChatRoom chatRoom = ChatRoom.builder()
                .mentor(mentor)
                .mentee(mentee)
                .reservation(reservation)
                .isClosed(false)
                .build();
        System.out.println("채팅방 : " + chatRoom.getId());
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        return ChatRoomResponseDto.of(savedChatRoom);
    }

    // 채팅방 목록 조히
    @Transactional(readOnly = true)
    public List<ChatRoomResponseDto> findAllChatRooms(Long userId) {

        List<ChatRoom> findChatRoomList = chatRoomRepository.findAllByMentorIdOrMenteeId(userId, userId);

        return findChatRoomList.stream().map(ChatRoomResponseDto::of)
                .toList();
    }
}
