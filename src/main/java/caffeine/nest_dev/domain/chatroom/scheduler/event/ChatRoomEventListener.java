package caffeine.nest_dev.domain.chatroom.scheduler.event;

import caffeine.nest_dev.domain.chatroom.scheduler.service.ChatRoomSchedulerService;
import caffeine.nest_dev.domain.chatroom.scheduler.service.ChatRoomTerminationSchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatRoomEventListener {

    private final ChatRoomTerminationSchedulerService schedulerService;
    private final ChatRoomSchedulerService chatRoomSchedulerService;

    // 채팅방 종료 이벤트
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void transactionSchedule(SaveTerminationRoomEvent event) {
        try {
            log.info("채팅방 종료 작업 트랜잭션 시작 - reservationId: {}", event.getReservationId());
            schedulerService.registerChatRoomCloseSchedule(event.getReservationId(), event.getEndAt());
            log.info("채팅방 종료 스케줄 등록 완료 - reservationId: {}", event.getReservationId());
        } catch (Exception e) {
            log.error("채팅방 종료 스케줄 등록 실패 - reservationId: {}", event.getReservationId(), e);
            // 필요시 보상 트랜잭션이나 알림 처리 추가
            throw e;
        }
    }

    // 채팅방 생성 이벤트
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void scheduleChatRoomCreationAfterPayment(SaveCreateRoomEvent event) {
        try {
            log.info("채팅방 생성 작업 트랜잭션 시작 - reservationId: {}", event.getReservationId());
            chatRoomSchedulerService.registerChatRoomSchedule(event.getReservationId(), event.getStartAt());
            log.info("채팅방 생성 스케줄 등록 완료 - reservationId: {}", event.getReservationId());
        } catch (Exception e) {
            log.error("채팅방 생성 스케줄 등록 실패 - reservationId: {}", event.getReservationId(), e);
            // 필요시 보상 트랜잭션이나 알림 처리 추가
            throw e;
        }
    }

}
