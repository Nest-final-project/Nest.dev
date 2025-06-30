package caffeine.nest_dev.domain.chatroom.scheduler.service;

import caffeine.nest_dev.domain.chatroom.scheduler.entity.ChatRoomSchedule;
import caffeine.nest_dev.domain.chatroom.scheduler.enums.ChatRoomType;
import caffeine.nest_dev.domain.chatroom.scheduler.enums.ScheduleStatus;
import caffeine.nest_dev.domain.chatroom.scheduler.repository.ChatRoomScheduleRepository;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomTerminationSchedulerService {

    // 스케줄링을 위한 빈
    private final TaskScheduler taskScheduler;
    private final ChatRoomScheduleRepository chatRoomScheduleRepository;
    private final ChatRoomTerminationRunService runService;

    // 서버 재시작 시 초기화 작업
    public void init() {
        LocalDateTime now = LocalDateTime.now();

        List<ChatRoomSchedule> scheduleList = chatRoomScheduleRepository.findAllByChatRoomTypeAndScheduleStatus(
                ChatRoomType.CLOSE, ScheduleStatus.PENDING);

        if (scheduleList.isEmpty()) {
            log.info("저장된 종료 예약 작업이 없습니다.");
            return;
        }
        log.info("예약 종료 스케줄 {}건", scheduleList.size());

        int scheduledCount = 0;
        int executedCount = 0;

        for (ChatRoomSchedule roomSchedule : scheduleList) {
            if (roomSchedule.getScheduledTime().isAfter(now)) {
                // 미래 시각이면 예약 등록
                taskScheduler.schedule(
                        disconnectUsersAndCloseRoom(roomSchedule.getId()),
                        Date.from(roomSchedule.getScheduledTime().atZone(ZoneId.systemDefault()).toInstant())
                );
                scheduledCount++;
            } else {
                // 이미 지난 예약이면 즉시 실행
                log.info("예약 시간이 지난 종료 스케줄을 즉시 실행합니다. 예약ID: {}", roomSchedule.getId());
                try {
                    runService.transactionRunnable(roomSchedule.getId());
                    executedCount++;
                } catch (Exception e) {
                    log.error("종료 작업 즉시 실행 실패. 예약ID: {}", roomSchedule.getId(), e);
                }
            }
        }

        log.info("종료 예약 재등록 완료. 등록: {}건, 즉시 실행: {}건", scheduledCount, executedCount);
    }

    /**
     * 채팅방 종료 스케줄 등록 메서드
     *
     * @param reservationId 예약 ID
     * @param endTime       예약 종료 시간 (채팅 종료 시간)
     */
    public void registerChatRoomCloseSchedule(Long reservationId, LocalDateTime endTime) {
        ChatRoomSchedule closeSchedule = ChatRoomSchedule.builder()
                .reservationId(reservationId)
                .scheduledTime(endTime)
                .scheduleStatus(ScheduleStatus.PENDING)
                .chatRoomType(ChatRoomType.CLOSE)
                .build();

        ChatRoomSchedule saved = chatRoomScheduleRepository.save(closeSchedule);

        // 종료 시각에 해당 채팅방을 닫는 작업 예약
        taskScheduler.schedule(
                disconnectUsersAndCloseRoom(saved.getId()),
                Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant())
        );
    }

    /**
     * 실제 채팅방을 종료하고 참여자의 세션을 닫는 작업을 Runnable 변환
     *
     * @param scheduleId 예약된 채팅 종료 작업 ID
     * @return Runnable
     */
    private Runnable disconnectUsersAndCloseRoom(Long scheduleId) {
        return () -> {
            runService.transactionRunnable(scheduleId);
        };
    }

}
