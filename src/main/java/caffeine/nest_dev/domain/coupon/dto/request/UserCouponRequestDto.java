package caffeine.nest_dev.domain.coupon.dto.request;

import caffeine.nest_dev.domain.coupon.entity.Coupon;
import caffeine.nest_dev.domain.coupon.entity.UserCoupon;
import caffeine.nest_dev.domain.coupon.entity.UserCouponId;
import caffeine.nest_dev.domain.coupon.enums.CouponUseStatus;
import caffeine.nest_dev.domain.user.entity.User;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserCouponRequestDto {
    
    @NotNull(message = "쿠폰 ID는 필수입니다")
    @Positive(message = "쿠폰 ID는 양수여야 합니다")
    private Long couponId;
    
    @NotNull(message = "사용자 ID는 필수입니다")
    @Positive(message = "사용자 ID는 양수여야 합니다")
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
