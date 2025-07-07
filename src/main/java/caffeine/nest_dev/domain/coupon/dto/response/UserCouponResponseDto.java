package caffeine.nest_dev.domain.coupon.dto.response;

import caffeine.nest_dev.domain.coupon.entity.UserCoupon;
import caffeine.nest_dev.domain.coupon.enums.CouponUseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class UserCouponResponseDto {

    private final Long couponId;
    private final Long userId;
    private final CouponUseStatus useStatus;
    
    // 쿠폰 상세 정보 추가
    private final String couponName;
    private final Integer discountAmount;
    private final LocalDateTime validFrom;
    private final LocalDateTime validTo;

    public static UserCouponResponseDto of(UserCoupon userCoupon) {
        return UserCouponResponseDto.builder()
                .couponId(userCoupon.getId().getCouponId())
                .userId(userCoupon.getId().getUserId())
                .useStatus(userCoupon.getIsUsed())
                .couponName(userCoupon.getCoupon().getName())
                .discountAmount(userCoupon.getCoupon().getDiscountAmount())
                .validFrom(userCoupon.getCoupon().getValidFrom())
                .validTo(userCoupon.getCoupon().getValidTo())
                .build();
    }
}
