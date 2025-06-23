package caffeine.nest_dev.domain.coupon.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor
@EqualsAndHashCode
public class UserCouponId implements Serializable {

    private Long value;

    @Column(name = "coupon_id")
    private Long couponId;

    @Column(name = "user_id")
    private Long userId;

    public UserCouponId(Long userId, Long couponId) {
        this.userId = userId;
        this.couponId = couponId;
    }
}

