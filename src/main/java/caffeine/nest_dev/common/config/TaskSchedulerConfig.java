package caffeine.nest_dev.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class TaskSchedulerConfig {

    public ThreadPoolTaskScheduler chatRoomScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(200);   // 스레드 풀의 스레드 수
        scheduler.setThreadNamePrefix("chatRoom-scheduler-"); // prefix
        scheduler.setWaitForTasksToCompleteOnShutdown(true); // 애플리케이션이 종료될 때, 실행중인 스케줄러 작업이 완료될 때까지 기다림
        return scheduler;
    }
}
