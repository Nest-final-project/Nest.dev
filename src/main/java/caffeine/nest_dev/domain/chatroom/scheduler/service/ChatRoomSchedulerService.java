package caffeine.nest_dev.domain.chatroom.scheduler.service;

import caffeine.nest_dev.common.enums.ErrorCode;
import caffeine.nest_dev.common.exception.BaseException;
import caffeine.nest_dev.domain.chatroom.dto.request.CreateChatRoomRequestDto;
import caffeine.nest_dev.domain.chatroom.dto.response.ChatRoomResponseDto;
import caffeine.nest_dev.domain.chatroom.scheduler.entity.ChatRoomSchedule;
import caffeine.nest_dev.domain.chatroom.scheduler.enums.ChatRoomType;
import caffeine.nest_dev.domain.chatroom.scheduler.enums.ScheduleStatus;
import caffeine.nest_dev.domain.chatroom.scheduler.repository.ChatRoomScheduleRepository;
import caffeine.nest_dev.domain.chatroom.service.ChatRoomService;
import caffeine.nest_dev.domain.reservation.entity.Reservation;
import caffeine.nest_dev.domain.reservation.repository.ReservationRepository;
import caffeine.nest_dev.domain.user.entity.User;
import caffeine.nest_dev.domain.user.service.UserService;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomSchedulerService {

    private final TaskScheduler taskScheduler;

    private final UserService userService;
    private final ChatRoomService chatRoomService;
    private final ChatRoomScheduleRepository scheduleRepository;
    private final ReservationRepository reservationRepository;

    // 알림 예약
    private final ChatRoomTerminationNotifier notifier;

    // 서버 재시작 시 다시 등록
    public void init() {
        // 예약중이던 작업 가져오기
        List<ChatRoomSchedule> findScheduleList = scheduleRepository.findAllByScheduleStatus(ScheduleStatus.PENDING);

        if (findScheduleList.isEmpty()) {
            log.info("저장된 작업이 없습니다.");
            return;
        }
        log.info("불러온 채팅방 생성 예약 작업 개수: {}", findScheduleList.size());

        // 예약시간이 지나지 않았다면 작업을 다시 등록함
        for (ChatRoomSchedule roomSchedule : findScheduleList) {

            if (roomSchedule.getScheduledTime().isAfter(LocalDateTime.now())) {
                startSchedule(roomSchedule);
                log.info("서버시작 후 실행되지 않은 작업이 등록되었습니다.");
            }
        }
    }

    // 예약 작업 저장
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registerChatRoomSchedule(Long reservationId, LocalDateTime startTime) {

        try {
            if (startTime.isBefore(LocalDateTime.now().minusSeconds(2))) {
                log.warn("지정된 시작 시간이 이미 지났습니다. 예약 ID: {}", reservationId);
                return;
            }

            ChatRoomSchedule roomSchedule = ChatRoomSchedule.builder()
                    .reservationId(reservationId)
                    .scheduledTime(startTime)
                    .scheduleStatus(ScheduleStatus.PENDING)
                    .chatRoomType(ChatRoomType.OPEN)
                    .build();

            ChatRoomSchedule saved = scheduleRepository.save(roomSchedule);
            startSchedule(saved);

        } catch (Exception e) {
            log.error("채팅방 예약 등록 실패 : {}", e.getMessage(), e);
        }

    }

    // 예약된 시간에 실행될 작업
    private Runnable createSchedule(Long scheduleId) {
        return () -> {
            ChatRoomSchedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(
                    () -> new BaseException(ErrorCode.CHATROOM_SCHEDULE_NOT_FOUND)
            );

            if (ScheduleStatus.COMPLETE.equals(schedule.getScheduleStatus())) {
                log.info("이미 완료된 스케줄입니다. ID : {}", scheduleId);
                return;
            }
            if (schedule.getScheduledTime().isBefore(LocalDateTime.now())) {
                log.warn("예약시간이 지났습니다. ID : {}", scheduleId);
            }

            Long reservationId = schedule.getReservationId();
            CreateChatRoomRequestDto requestDto = new CreateChatRoomRequestDto(reservationId);

            // 알림 예약 작업을 등록하기 위해 responseDto 반환
            ChatRoomResponseDto responseDto = chatRoomService.createChatRooms(requestDto);

            schedule.updateStatus();
            scheduleRepository.save(schedule);

            registerNotification(responseDto, reservationId);

            log.info("채팅방 생성 완료 예약 ID : {}", reservationId);
        };
    }

    // 알림 예약 작업 등록
    private void registerNotification(ChatRoomResponseDto responseDto, Long reservationId) {
        User mentee = userService.findByIdAndIsDeletedFalseOrElseThrow(responseDto.getMenteeId());
        User mentor = userService.findByIdAndIsDeletedFalseOrElseThrow(responseDto.getMentorId());

        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(
                () -> new BaseException(ErrorCode.RESERVATION_NOT_FOUND)
        );

        notifier.registerNotificationSchedule(
                responseDto.getRoomId(),
                reservation.getReservationEndAt(),
                mentor
        );
        notifier.registerNotificationSchedule(
                responseDto.getRoomId(),
                reservation.getReservationEndAt(),
                mentee
        );
    }

    // 스케줄 시작
    private void startSchedule(ChatRoomSchedule roomSchedule) {
        taskScheduler.schedule(
                createSchedule(roomSchedule.getId()),
                Date.from(roomSchedule.getScheduledTime().atZone(ZoneId.systemDefault()).toInstant()));
    }


}
