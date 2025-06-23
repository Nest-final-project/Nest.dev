package caffeine.nest_dev.domain.coupon.entity;

import caffeine.nest_dev.common.entity.BaseEntity;
import caffeine.nest_dev.domain.coupon.dto.request.UserCouponRequestDto;
import caffeine.nest_dev.domain.coupon.enums.CouponUseStatus;
import caffeine.nest_dev.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Table(name = "user_coupons")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserCoupon extends BaseEntity {

    @EmbeddedId
    private UserCouponId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("couponId")
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private CouponUseStatus isUsed;

    public boolean isUsed() {
        return this.isUsed == CouponUseStatus.USED;
    }

    public void markAsUsed() {
        this.isUsed = CouponUseStatus.USED;
    }

    public void unmarkAsUsed() {
        this.isUsed = CouponUseStatus.UNUSED;
    }

    public void modifyUseStatus(UserCouponRequestDto requestDto) {
        if (requestDto.getIsUsed() != null) {
            this.isUsed = requestDto.getIsUsed();
        }
    }
}
