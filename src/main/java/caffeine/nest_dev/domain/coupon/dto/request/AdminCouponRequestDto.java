package caffeine.nest_dev.domain.coupon.dto.request;

import caffeine.nest_dev.domain.coupon.entity.Coupon;
import caffeine.nest_dev.domain.coupon.enums.CouponDiscountType;
import caffeine.nest_dev.domain.user.enums.UserGrade;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminCouponRequestDto {

    private String name;
    private Integer discountAmount;
    private Integer totalQuantity;
    private Integer issuedQuantity;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private UserGrade minGrade;
    private Integer minOrderAmount;
    private CouponDiscountType discountType;

    public Coupon toEntity() {
        return Coupon.builder()
                .name(name)
                .discountAmount(discountAmount)
                .totalQuantity(totalQuantity)
                .issuedQuantity(issuedQuantity)
                .validFrom(validFrom)
                .validTo(validTo)
                .minGrade(minGrade)
                .minOrderAmount(minOrderAmount)
                .discountType(discountType)
                .build();
    }
}
