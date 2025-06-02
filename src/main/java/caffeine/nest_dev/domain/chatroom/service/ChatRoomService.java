package caffeine.nest_dev.domain.chatroom.service;

import caffeine.nest_dev.domain.chatroom.dto.request.CreateChatRoomRequestDto;
import caffeine.nest_dev.domain.chatroom.dto.response.ChatRoomResponseDto;
import caffeine.nest_dev.domain.chatroom.entity.ChatRoom;
import caffeine.nest_dev.domain.chatroom.repository.ChatRoomRepository;
import caffeine.nest_dev.domain.reservation.entity.Reservation;
import caffeine.nest_dev.domain.reservation.repository.ReservationRepository;
import caffeine.nest_dev.domain.user.entity.User;
import java.util.List;
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

        // TODO : 존재하는 채팅방 일 경우 기존의 채팅방을 반환하는 로직 추가
        // 예약이 유효한지 확인
        Reservation reservation = reservationRepository.findById(requestDto.getReservationId()).orElseThrow(
                () -> new IllegalArgumentException("예약이 존재하지 않습니다.")
        );
        // 멘토, 멘티 정보 추출
        User mentor = reservation.getMentor();
        User mentee = reservation.getMentee();

        ChatRoom chatRoom = ChatRoom.builder()
                .mentor(mentor)
                .mentee(mentee)
                .reservation(reservation)
                .isClosed(false)
                .build();

        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        return ChatRoomResponseDto.of(savedChatRoom);
    }

    @Transactional(readOnly = true)
    public List<ChatRoomResponseDto> findAllChatRooms(Long userId) {
        // 본인의 id를 가지고있는 채팅방 목록을 가져와야함      ????
        // 그런데 만약에 자기가 멘티인데 멘토의 목록을 가져올수도 있잖아
        // 어 그런데 우리 멘토멘티 전환 안되면 ?
        return null;
    }
}
