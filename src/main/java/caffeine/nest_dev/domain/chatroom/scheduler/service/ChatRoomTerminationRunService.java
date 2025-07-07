package caffeine.nest_dev.domain.chatroom.scheduler.service;

import caffeine.nest_dev.common.enums.ErrorCode;
import caffeine.nest_dev.common.exception.BaseException;
import caffeine.nest_dev.common.websocket.util.WebSocketSessionRegistry;
import caffeine.nest_dev.domain.chatroom.entity.ChatRoom;
import caffeine.nest_dev.domain.chatroom.repository.ChatRoomRepository;
import caffeine.nest_dev.domain.chatroom.scheduler.entity.ChatRoomSchedule;
import caffeine.nest_dev.domain.chatroom.scheduler.enums.ScheduleStatus;
import caffeine.nest_dev.domain.chatroom.scheduler.repository.ChatRoomScheduleRepository;
import caffeine.nest_dev.domain.reservation.entity.Reservation;
import caffeine.nest_dev.domain.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomTerminationRunService {

    private final ChatRoomScheduleRepository chatRoomScheduleRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ReservationRepository reservationRepository;

    // 사용자 Id , Session 매핑한 저장소
    private final WebSocketSessionRegistry sessionRegistry;


    @Transactional
    public void transactionRunnable(Long scheduleId) {
        ChatRoomSchedule schedule = chatRoomScheduleRepository.findById(scheduleId).orElseThrow(
                () -> new BaseException(ErrorCode.CHATROOM_SCHEDULE_NOT_FOUND)
        );

        if (ScheduleStatus.COMPLETE.equals(schedule.getScheduleStatus())) {
            log.info("이미 완료된 스케줄입니다. ID: {}", scheduleId);
            return;
        }

        // 종료 후 상태 업데이트
        schedule.updateStatus();

        Long reservationId = schedule.getReservationId();
        ChatRoom chatRoom = chatRoomRepository.findByReservationId(reservationId).orElseThrow(
                () -> new BaseException(ErrorCode.CHATROOM_NOT_FOUND)
        );

        // isClosed == true 설정
        chatRoom.close();

        // 사용자별 세션 종료
        String mentorId = chatRoom.getMentor().getId().toString();
        String menteeId = chatRoom.getMentee().getId().toString();

        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(
                () -> new BaseException(ErrorCode.RESERVATION_NOT_FOUND)
        );
        reservation.complete();

        log.info("종료 작업 완료 : ScheduleId = {}", scheduleId);
        log.info("채팅방 종료 완료 : ChatRoomId = {}, mentor = {}, mentee = {}", chatRoom.getId(), mentorId, menteeId);
    }
}
