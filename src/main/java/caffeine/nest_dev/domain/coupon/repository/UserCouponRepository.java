package caffeine.nest_dev.domain.coupon.repository;

import caffeine.nest_dev.domain.coupon.entity.Coupon;
import caffeine.nest_dev.domain.coupon.entity.UserCoupon;
import caffeine.nest_dev.domain.coupon.entity.UserCouponId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface UserCouponRepository extends CrudRepository<UserCoupon, UserCouponId> {
    // FK로 묶인 user_coupon 먼저 삭제
    void deleteByCoupon(Coupon coupon);
    
    Page<UserCoupon> findAll(Pageable pageable);
    
    // 쿠폰 정보를 함께 조회하는 메서드 추가
    @Query("SELECT uc FROM UserCoupon uc JOIN FETCH uc.coupon WHERE uc.id.userId = :userId")
    Page<UserCoupon> findByUserIdWithCoupon(Long userId, Pageable pageable);
    
    // 모든 사용자 쿠폰을 쿠폰 정보와 함께 조회
    @Query("SELECT uc FROM UserCoupon uc JOIN FETCH uc.coupon")
    Page<UserCoupon> findAllWithCoupon(Pageable pageable);
}
