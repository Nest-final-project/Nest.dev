package caffeine.nest_dev.domain.coupon.scheduler;

import caffeine.nest_dev.domain.coupon.entity.Coupon;
import caffeine.nest_dev.domain.coupon.repository.AdminCouponRepository;
import caffeine.nest_dev.domain.coupon.repository.UserCouponRepository;
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

        // validTo가 지난 쿠폰 찾기
        List<Coupon> expiredCoupons = adminCouponRepository.findByValidToBefore(now);

        if (expiredCoupons.isEmpty()) {
            return;
        }

        // 쿠폰마다 관련 user_coupon 먼저 삭제
        for (Coupon coupon : expiredCoupons) {
            userCouponRepository.deleteByCoupon(coupon);
        }

        // 쿠폰 삭제하기
        adminCouponRepository.deleteAll(expiredCoupons);
    }
}
