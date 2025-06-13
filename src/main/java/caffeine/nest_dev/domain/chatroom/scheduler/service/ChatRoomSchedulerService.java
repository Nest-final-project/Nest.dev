package caffeine.nest_dev.domain.chatroom.scheduler.service;

import caffeine.nest_dev.domain.chatroom.dto.request.CreateChatRoomRequestDto;
import caffeine.nest_dev.domain.chatroom.scheduler.entity.ChatRoomSchedule;
import caffeine.nest_dev.domain.chatroom.scheduler.enums.ChatRoomType;
import caffeine.nest_dev.domain.chatroom.scheduler.enums.ScheduleStatus;
import caffeine.nest_dev.domain.chatroom.scheduler.repository.ChatRoomScheduleRepository;
import caffeine.nest_dev.domain.chatroom.service.ChatRoomService;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatRoomSchedulerService {

    private final TaskScheduler taskScheduler;

    private final ChatRoomService chatRoomService;
    private final ChatRoomScheduleRepository scheduleRepository;

    // 서버 재시작 시 다시 등록
    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        // 예약중이던 작업 가져오기
        List<ChatRoomSchedule> findScheduleList = scheduleRepository.findAllByScheduleStatus(ScheduleStatus.PENDING);

        if (findScheduleList.isEmpty()) {
            log.info("저장된 작업이 없습니다.");
            return;
        }
        log.info("저장된 예약 작업 불러오기 schedule : {}", findScheduleList.isEmpty());
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

    // 예약된 시간에 실행될 작업
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
            chatRoomService.createChatRooms(requestDto);

            schedule.updateStatus();
            scheduleRepository.save(schedule);

            log.info("채팅방 생성 완료 예약 ID : {}", schedule.getReservationId());

        };
    }


}
