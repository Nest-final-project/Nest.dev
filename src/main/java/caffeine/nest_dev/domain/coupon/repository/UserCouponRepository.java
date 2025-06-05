package caffeine.nest_dev.domain.coupon.repository;

import caffeine.nest_dev.domain.coupon.entity.Coupon;
import caffeine.nest_dev.domain.coupon.entity.UserCoupon;
import caffeine.nest_dev.domain.coupon.entity.UserCouponId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface UserCouponRepository extends CrudRepository<UserCoupon, UserCouponId> {
    // FK로 묶인 user_coupon 먼저 삭제
    void deleteByCoupon(Coupon coupon);
    Page<UserCoupon> findAll(Pageable pageable);
}
