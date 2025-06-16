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

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatRoomSchedulerService {

    private final TaskScheduler taskScheduler;

    private final ChatRoomService chatRoomService;
    private final ChatRoomScheduleRepository scheduleRepository;
    private final ReservationRepository reservationRepository;
    private final UserService userService;

    // 알림 예약
    private final ChatRoomTerminationNotifier notifier;

    /**
     * 서버가 재시작될 때 예약된 채팅방 생성 작업을 다시 스케줄링합니다.
     *
     * 저장소에서 상태가 PENDING인 모든 채팅방 생성 예약 작업을 조회하여,
     * 예약 시간이 아직 지나지 않은 작업에 한해 TaskScheduler에 재등록합니다.
     * 예약된 작업이 없거나 모두 만료된 경우 별도의 작업을 수행하지 않습니다.
     */
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
                taskScheduler.schedule(createSchedule(roomSchedule.getId()),
                        Date.from(roomSchedule.getScheduledTime().atZone(ZoneId.systemDefault()).toInstant()));
                log.info("서버시작 후 실행되지 않은 작업이 등록되었습니다.");
            }
        }
    }

    // 예약 작업 저장
    public void registerChatRoomSchedule(Long reservationId, LocalDateTime startTime) {

        ChatRoomSchedule roomSchedule = ChatRoomSchedule.builder()
                .reservationId(reservationId)
                .scheduledTime(startTime)
                .scheduleStatus(ScheduleStatus.PENDING)
                .chatRoomType(ChatRoomType.OPEN)
                .build();

        ChatRoomSchedule saved = scheduleRepository.save(roomSchedule);

        taskScheduler.schedule(
                createSchedule(saved.getId()),
                Date.from(roomSchedule.getScheduledTime().atZone(ZoneId.systemDefault()).toInstant()));

    }

    /**
     * 예약된 스케줄 ID에 따라 채팅방을 생성하고, 관련 알림 예약을 등록하는 작업을 반환합니다.
     *
     * 반환된 Runnable은 실행 시 해당 스케줄의 상태를 확인하여 이미 완료된 경우 작업을 중단하며,
     * 완료되지 않은 경우 채팅방을 생성하고 스케줄 상태를 완료로 변경한 뒤, 참여자에게 채팅방 종료 알림 예약을 등록합니다.
     *
     * @param scheduleId 예약된 채팅방 스케줄의 ID
     * @return 예약된 시간에 실행될 채팅방 생성 및 알림 예약 작업
     */
    private Runnable createSchedule(Long scheduleId) {
        return () -> {
            ChatRoomSchedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(
                    () -> new IllegalArgumentException("예약된 작업이 없습니다.")
            );

            if (ScheduleStatus.COMPLETE.equals(schedule.getScheduleStatus())) {
                log.info("이미 완료된 스케줄입니다. ID : {}", scheduleId);
                return;
            }

            CreateChatRoomRequestDto requestDto = new CreateChatRoomRequestDto(schedule.getReservationId());
            // 알림 예약 작업을 등록하기 위해 responseDto 반환
            ChatRoomResponseDto responseDto = chatRoomService.createChatRooms(requestDto);

            Long reservationId = schedule.getReservationId();

            schedule.updateStatus();
            scheduleRepository.save(schedule);

            registerNotification(responseDto, reservationId);

            log.info("채팅방 생성 완료 예약 ID : {}", reservationId);

        };
    }

    /**
     * 채팅방 종료 시점에 멘토와 멘티에게 알림 예약을 등록합니다.
     *
     * @param responseDto 생성된 채팅방 및 참여자 정보를 담은 DTO
     * @param reservationId 알림 예약을 위한 예약 ID
     *
     * @throws BaseException 예약 정보를 찾을 수 없는 경우 발생합니다.
     */
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


}
