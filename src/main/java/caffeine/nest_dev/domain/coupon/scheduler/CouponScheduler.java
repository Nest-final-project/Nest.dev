package caffeine.nest_dev.domain.coupon.scheduler;

import caffeine.nest_dev.domain.coupon.entity.Coupon;
import caffeine.nest_dev.domain.coupon.repository.AdminCouponRepository;
import caffeine.nest_dev.domain.coupon.repository.UserCouponRepository;
import caffeine.nest_dev.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CouponScheduler {

    private final AdminCouponRepository adminCouponRepository;
    private final UserCouponRepository userCouponRepository;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void deleteExpiredCoupons() {
        LocalDateTime now = LocalDateTime.now();

        // validTo가 지난 쿠폰 찾기 (Coupon Entity의 validTo가 now보다 과거(쿠폰 만료)인 쿠폰 리스 조회)
        List<Coupon> expiredCoupons = adminCouponRepository.findByValidToBefore(now);

        if (expiredCoupons.isEmpty()) {
            return;
        }

        // 쿠폰마다 관련 user_coupon 먼저 삭제 (외래키 제약 조건 때문에 반드시 먼저 삭제 해야함)
        for (Coupon coupon : expiredCoupons) {
            userCouponRepository.deleteByCoupon(coupon);
        }

        // 쿠폰 삭제하기
        adminCouponRepository.deleteAll(expiredCoupons);
        System.out.println("만료 쿠폰 삭제 완료 : " + expiredCoupons.size() + "개");
    }
}
