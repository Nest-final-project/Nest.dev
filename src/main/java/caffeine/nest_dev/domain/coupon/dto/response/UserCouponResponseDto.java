package caffeine.nest_dev.domain.coupon.dto.response;

import caffeine.nest_dev.domain.coupon.entity.UserCoupon;
import caffeine.nest_dev.domain.coupon.enums.CouponUseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserCouponResponseDto {

    private final Long couponId;
    private final Long userId;
    private final CouponUseStatus useStatus;

    public static UserCouponResponseDto of(UserCoupon userCoupon) {
        return UserCouponResponseDto.builder()
                .couponId(userCoupon.getId().getCouponId())
                .userId(userCoupon.getId().getUserId())
                .useStatus(userCoupon.getIsUsed())
                .build();
    }
}
