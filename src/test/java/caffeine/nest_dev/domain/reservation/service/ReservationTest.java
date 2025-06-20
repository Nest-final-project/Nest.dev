package caffeine.nest_dev.domain.reservation.service;

import caffeine.nest_dev.common.config.WebSocketConfig;
import caffeine.nest_dev.common.exception.BaseException;
import caffeine.nest_dev.domain.reservation.dto.request.ReservationRequestDto;
import caffeine.nest_dev.domain.reservation.enums.ReservationStatus;
import caffeine.nest_dev.domain.reservation.repository.ReservationRepository;
import caffeine.nest_dev.domain.user.entity.User;
import caffeine.nest_dev.domain.user.enums.SocialType;
import caffeine.nest_dev.domain.user.enums.UserGrade;
import caffeine.nest_dev.domain.user.enums.UserRole;
import caffeine.nest_dev.domain.user.repository.UserRepository;
import com.esotericsoftware.minlog.Log;
import jakarta.validation.constraints.AssertTrue;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@ImportAutoConfiguration(exclude = WebSocketConfig.class)
public class ReservationTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ReservationRepository reservationRepository;

    @BeforeEach
    void setUp() {
        for (int i = 1; i <= 1000; i++) {
            User mentee = User.builder()
                    .name("이멘티" + i)
                    .email("mentee" + i + "@naver.com")
                    .nickName("이고수" + i)
                    .password("!@#1Qwer")
                    .phoneNumber("010-1111-" + String.format("%04d", i))
                    .socialType(SocialType.LOCAL)
                    .userRole(UserRole.MENTEE)
                    .userGrade(UserGrade.SEED)
                    .build();
            userRepository.save(mentee);
        }
    }


    @Test
    @DisplayName("AOP 를 활용한 분산락")
    void concurrencyTestWithAop() {

        LocalDateTime startAt = LocalDateTime.of(2025, 6, 15, 10, 0);
        LocalDateTime endAt = LocalDateTime.of(2025, 6, 15, 10, 30);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        ReservationRequestDto dto = new ReservationRequestDto(
                5L, 1L, ReservationStatus.REQUESTED, startAt, endAt);

        System.out.println("\n\n\n\n[concurrencyTestWithAOP]");
        IntStream.range(1, 1000).parallel().forEach(i -> {
            try{
                reservationService.save((long) i, dto);
                successCount.incrementAndGet();

            }catch(BaseException e){
                Log.error("실패: menteeId = " + i + ", 메시지 = " + e.getMessage());
                failCount.incrementAndGet();
            }
        });
        System.out.println("성공: " + successCount.get());
        System.out.println("실패: " + failCount.get());
    }

}
