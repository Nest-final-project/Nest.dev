package caffeine.nest_dev.domain.coupon.entity;

import caffeine.nest_dev.common.enums.ErrorCode;
import caffeine.nest_dev.common.exception.BaseException;
import caffeine.nest_dev.domain.coupon.dto.request.AdminCouponRequestDto;
import caffeine.nest_dev.domain.coupon.enums.CouponDiscountType;
import caffeine.nest_dev.domain.user.enums.UserGrade;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Getter
@Entity
@Table(name = "coupons")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer discountAmount; // 할인 금액

    @Column(nullable = false)
    private Integer totalQuantity; // 전체 발급 가능 수량

    @Column(nullable = false)
    private Integer issuedQuantity = 0; // 현재 발급된 수량

    @Column(nullable = false)
    private LocalDateTime validFrom; // 유효 시작일

    @Column(nullable = false)
    private LocalDateTime validTo; // 유효 종료일

    @Enumerated(EnumType.STRING)
    private UserGrade minGrade;   // 발급 가능 최소 등급

    @Column(nullable = false)
    private Integer minOrderAmount;  // 최소 주문 금액

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponDiscountType discountType;  // 할인 타입

    public void modifyCoupon(AdminCouponRequestDto requestDto) {
        if (requestDto.getName() != null) {
            this.name = requestDto.getName();
        }
        if (requestDto.getDiscountAmount() != null) {
            this.discountAmount = requestDto.getDiscountAmount();
        }
        if (requestDto.getTotalQuantity() != null) {
            this.totalQuantity = requestDto.getTotalQuantity();
        }
        if (requestDto.getIssuedQuantity() != null) {
            this.issuedQuantity = requestDto.getIssuedQuantity();
        }
        if (requestDto.getValidFrom() != null) {
            this.validFrom = requestDto.getValidFrom();
        }
        if (requestDto.getValidTo() != null) {
            this.validTo = requestDto.getValidTo();
        }
        if (requestDto.getMinGrade() != null) {
            this.minGrade = requestDto.getMinGrade();
        }
        if (requestDto.getMinOrderAmount() != null) {
            this.minOrderAmount = requestDto.getMinOrderAmount();
        }
        if (requestDto.getDiscountType() != null) {
            this.discountType = requestDto.getDiscountType();
        }
    }

    public void issue() {
        if (this.totalQuantity == null || this.issuedQuantity == null) {
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        if (this.totalQuantity <= 0) {
            throw new BaseException(ErrorCode.COUPON_OUT_OF_STOCK);
        }
        this.totalQuantity -= 1;
        this.issuedQuantity += 1;
    }

    public boolean canIssue() {
        return this.totalQuantity != null
                && this.issuedQuantity != null
                && this.totalQuantity > 0
                && this.issuedQuantity < this.totalQuantity;
    }

    public void validateForUse(BigDecimal orderAmount) {
        if (orderAmount.compareTo(BigDecimal.valueOf(this.minOrderAmount)) < 0) {
            throw new BaseException(ErrorCode.COUPON_MIN_ORDER_AMOUNT_NOT_MET);
        }
    }
}
