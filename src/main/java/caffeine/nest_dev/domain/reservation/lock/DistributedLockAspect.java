package caffeine.nest_dev.domain.reservation.lock;

import caffeine.nest_dev.common.enums.ErrorCode;
import caffeine.nest_dev.common.exception.BaseException;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class DistributedLockAspect {

    private final RedissonClient redissonClient;
    private final PlatformTransactionManager transactionManager;

    @Around("@annotation(distributedLock)")
    public Object lock(ProceedingJoinPoint joinPoint, DistributedLock distributedLock)
            throws Throwable {
        String key = distributedLock.key();
        long waitTime = distributedLock.waitTime();
        long leaseTime = distributedLock.leaseTime();

        RLock rlock = redissonClient.getLock(key); //락 획득할 준비

        boolean isLocked = false;
        try{
            isLocked = rlock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS); // 락 점유하기 위함
            if(!isLocked){ // 대기중(wait time)
                throw new BaseException(ErrorCode.LOCK_FAILED);
            }

            log.info("락 획득 성공: key = {} ", key);
            TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
            transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

            return transactionTemplate.execute(status -> { // 트랜잭션 시작
                try{
                    return joinPoint.proceed();
                }catch(BaseException e) { // 예외 발생 시
                    status.setRollbackOnly();
                    throw e;
                }catch (Throwable throwable){
                    status.setRollbackOnly();
                    throw new RuntimeException(throwable);
                }
            }); // 트랜잭션 커밋
        } finally {
            if(isLocked && rlock.isHeldByCurrentThread()){
                rlock.unlock();
                log.info("락 해제: key = {}", key);
            }
        }
    }
}
