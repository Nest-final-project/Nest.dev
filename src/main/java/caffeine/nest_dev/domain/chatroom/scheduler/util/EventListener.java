package caffeine.nest_dev.domain.chatroom.scheduler.util;

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
public class EventListener {

    private final ChatRoomTerminationSchedulerService schedulerService;
    private final ChatRoomSchedulerService chatRoomSchedulerService;

    // 채팅방 종료 이벤트
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void transactionSchedule(SaveTerminationRoomEvent event) {
        log.info("채팅방 종료 작업 트랜잭션 시작");
        schedulerService.registerChatRoomCloseSchedule(event.getReservationId(), event.getEndAt());
    }

    // 채팅방 생성 이벤트
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void scheduleChatRoomCreationAfterPayment(SaveCreateRoomEvent event) {
        chatRoomSchedulerService.registerChatRoomSchedule(event.getReservationId(), event.getStartAt());
    }

}
