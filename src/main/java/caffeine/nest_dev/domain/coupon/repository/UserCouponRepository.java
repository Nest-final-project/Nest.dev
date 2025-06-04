package caffeine.nest_dev.domain.coupon.repository;

import caffeine.nest_dev.domain.coupon.entity.Coupon;
import caffeine.nest_dev.domain.coupon.entity.UserCoupon;
import org.springframework.data.repository.CrudRepository;

public interface UserCouponRepository extends CrudRepository<UserCoupon, Long> {
    // FK로 묶인 user_coupon 먼저 삭제
    void deleteByCoupon(Coupon coupon);
}
