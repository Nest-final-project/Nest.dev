package caffeine.nest_dev.domain.coupon.entity;

import caffeine.nest_dev.domain.coupon.enums.CouponUseStatus;
import jakarta.persistence.*;
import lombok.*;

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
    private Integer issuedQuantity; // 현재 발급된 수량

    @Column(nullable = false)
    private LocalDateTime validFrom; // 유효 시작일

    @Column(nullable = false)
    private LocalDateTime validTo; // 유효 종료일

    @Enumerated(EnumType.STRING)
    private CouponUseStatus minGrade;   // 발급 가능 최소 등급

}
