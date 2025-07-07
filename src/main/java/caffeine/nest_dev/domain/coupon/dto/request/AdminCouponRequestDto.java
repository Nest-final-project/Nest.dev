package caffeine.nest_dev.domain.coupon.dto.request;

import caffeine.nest_dev.domain.coupon.entity.Coupon;
import caffeine.nest_dev.domain.coupon.enums.CouponDiscountType;
import caffeine.nest_dev.domain.coupon.enums.CouponUseStatus;
import caffeine.nest_dev.domain.user.enums.UserGrade;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminCouponRequestDto {

    @NotBlank(message = "쿠폰명은 필수입니다")
    @Size(min = 2, max = 50, message = "쿠폰명은 2-50자 사이여야 합니다")
    private String name;
    
    @NotNull(message = "할인 금액은 필수입니다")
    @Min(value = 100, message = "최소 할인 금액은 100원입니다")
    private Integer discountAmount;
    
    @NotNull(message = "총 발급 수량은 필수입니다")
    private Integer totalQuantity;
    
    @Min(value = 0, message = "발급된 수량은 0 이상이어야 합니다")
    private Integer issuedQuantity;
    
    @NotNull(message = "유효 시작일은 필수입니다")
    private LocalDateTime validFrom;
    
    @NotNull(message = "유효 종료일은 필수입니다")
    private LocalDateTime validTo;
    
    @NotNull(message = "최소 사용자 등급은 필수입니다")
    private UserGrade minGrade;
    
    @NotNull(message = "최소 주문 금액은 필수입니다")
    @Min(value = 0, message = "최소 주문 금액은 0 이상이어야 합니다")
    private Integer minOrderAmount;
    
    @NotNull(message = "할인 유형은 필수입니다")
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
