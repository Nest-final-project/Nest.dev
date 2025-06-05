package caffeine.nest_dev.domain.coupon.dto.request;

import caffeine.nest_dev.domain.coupon.entity.Coupon;
import caffeine.nest_dev.domain.coupon.entity.UserCoupon;
import caffeine.nest_dev.domain.coupon.entity.UserCouponId;
import caffeine.nest_dev.domain.coupon.enums.CouponUseStatus;
import caffeine.nest_dev.domain.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserCouponRequestDto {
    private Long couponId;
    private Long userId;
    private CouponUseStatus isUsed;

    public UserCoupon toEntity(Coupon coupon, User user) {
        return UserCoupon.builder()
                .id(new UserCouponId(coupon.getId(), user.getId()))
                .coupon(coupon)
                .user(user)
                .isUsed(isUsed)
                .build();
    }
}
