package caffeine.nest_dev.domain.coupon.repository;

import caffeine.nest_dev.domain.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
}
