package caffeine.nest_dev.domain.chatroom.scheduler.service;

import caffeine.nest_dev.domain.chatroom.scheduler.entity.NotificationSchedule;
import caffeine.nest_dev.domain.chatroom.scheduler.repository.NotificationScheduleRepository;
import caffeine.nest_dev.domain.notification.service.NotificationService;
import caffeine.nest_dev.domain.user.entity.User;
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
public class ChatRoomTerminationNotifier {

    private final TaskScheduler taskScheduler;
    private final NotificationService notificationService;
    private final NotificationScheduleRepository scheduleRepository;

    /**
     * 서버 시작 시 아직 전송되지 않은 알림 예약 작업을 불러와 예약 시간이 미래인 경우 작업 스케줄러에 등록합니다.
     */
    public void init() {
        List<NotificationSchedule> findScheduleList = scheduleRepository.findAllByIsSent(false);

        if (findScheduleList.isEmpty()) {
            log.info("저장된 작업이 없습니다.");
            return;
        }
        log.info("불러온 알림 예약 작업 개수: {}", findScheduleList.size());

        for (NotificationSchedule schedule : findScheduleList) {

            if (schedule.getScheduledAt().isAfter(LocalDateTime.now())) {
                taskScheduler.schedule(createNotification(schedule.getId()),
                        Date.from(schedule.getScheduledAt().atZone(ZoneId.systemDefault()).toInstant()));
                log.info("서버시작 후 실행되지 않은 작업이 등록되었습니다.");
            }
        }
    }


    /**
     * 채팅방 종료 5분 전에 알림을 예약합니다.
     *
     * @param chatRoomId 알림을 예약할 채팅방의 ID
     * @param endTime 채팅방 종료 시각
     * @param user 알림을 받을 사용자
     */
    public void registerNotificationSchedule(Long chatRoomId, LocalDateTime endTime, User user) {
        NotificationSchedule schedule = NotificationSchedule.builder()
                .chatRoomId(chatRoomId)
                .scheduledAt(endTime.minusMinutes(5))
                .receiver(user)
                .build();

        NotificationSchedule savedSchedule = scheduleRepository.save(schedule);

        taskScheduler.schedule(
                createNotification(savedSchedule.getId()),
                Date.from(savedSchedule.getScheduledAt().atZone(ZoneId.systemDefault()).toInstant())
        );
    }


    /**
     * 지정된 알림 스케줄 ID에 따라 채팅방 종료 5분 전 알림을 전송하는 Runnable을 반환합니다.
     *
     * 반환된 Runnable은 스케줄이 이미 완료된 경우 아무 작업도 하지 않으며,
     * 완료되지 않은 경우 수신자에게 알림을 전송하고, 스케줄을 전송 완료로 갱신하여 저장합니다.
     *
     * @param scheduleId 알림 스케줄의 식별자
     * @return 알림 전송 작업을 수행하는 Runnable
     * @throws IllegalArgumentException 해당 ID의 스케줄이 존재하지 않을 경우 발생
     */
    private Runnable createNotification(Long scheduleId) {
        return () -> {
            NotificationSchedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(
                    () -> new IllegalArgumentException("저장된 작업이 없습니다.")
            );

            if (schedule.isSent()) {
                log.info("이미 완료된 스케줄입니다. ID : {}", scheduleId);
                return;
            }

            notificationService.send(schedule.getReceiver(), "채팅 종료까지 5분 남았습니다.");

            // isSent : false -> true, 전송시간 기록
            schedule.update();

            scheduleRepository.save(schedule);

            log.info("알림 전송 완료. 채팅방 ID : {}, 알림 전송 시간: {}", schedule.getChatRoomId(), schedule.getSentAt());
        };
    }
}
