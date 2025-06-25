package caffeine.nest_dev.domain.chatroom.scheduler.util;

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

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void transactionSchedule(SaveSchedulerEvent event) {
        log.info("채팅방 종료 트랜잭션 시작");
        schedulerService.registerChatRoomCloseSchedule(event.getReservationId(), event.getEndAt());
    }
}
