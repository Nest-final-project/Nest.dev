package caffeine.nest_dev.domain.coupon.service;

import caffeine.nest_dev.common.dto.PagingResponse;
import caffeine.nest_dev.common.enums.ErrorCode;
import caffeine.nest_dev.common.exception.BaseException;
import caffeine.nest_dev.domain.coupon.dto.request.UserCouponRequestDto;
import caffeine.nest_dev.domain.coupon.dto.response.UserCouponResponseDto;
import caffeine.nest_dev.domain.coupon.entity.Coupon;
import caffeine.nest_dev.domain.coupon.entity.UserCoupon;
import caffeine.nest_dev.domain.coupon.entity.UserCouponId;
import caffeine.nest_dev.domain.coupon.enums.CouponDiscountType;
import caffeine.nest_dev.domain.coupon.repository.AdminCouponRepository;
import caffeine.nest_dev.domain.coupon.repository.UserCouponRepository;
import caffeine.nest_dev.domain.reservation.lock.DistributedLock;
import caffeine.nest_dev.domain.user.entity.User;
import caffeine.nest_dev.domain.user.service.UserService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class UserCouponService {

    private final UserCouponRepository userCouponRepository;
    private final AdminCouponRepository adminCouponRepository;
    private final UserService userService;

    @DistributedLock(key = "'coupon-issue:' + #requestDto.couponId")
    public UserCouponResponseDto saveUserCoupon(UserCouponRequestDto requestDto) {
        Coupon coupon = adminCouponRepository.findById(requestDto.getCouponId())
                .orElseThrow(() -> new BaseException(
                        ErrorCode.NOT_FOUND_USER_COUPON));
        User user = userService.findByIdAndIsDeletedFalseOrElseThrow(requestDto.getUserId());
        coupon.issue();
        UserCoupon userCoupon = requestDto.toEntity(coupon, user);
        userCouponRepository.save(userCoupon);
        return UserCouponResponseDto.of(userCoupon);
    }

    @Transactional(readOnly = true)
    public PagingResponse<UserCouponResponseDto> getUserCoupon(Pageable pageable) {
        // 쿠폰 정보를 함께 조회하여 N+1 문제 해결
        Page<UserCoupon> pagingResponse = userCouponRepository.findAllWithCoupon(pageable);
        Page<UserCouponResponseDto> responseDtos = pagingResponse.map(UserCouponResponseDto::of);
        return PagingResponse.from(responseDtos);
    }

    @Transactional
    public void modifyUserCoupon(UserCouponRequestDto requestDto) {
        UserCouponId userCouponId = new UserCouponId(requestDto.getCouponId(), requestDto.getUserId());
        UserCoupon userCoupon = userCouponRepository.findById(userCouponId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_USER_COUPON));
        userCoupon.modifyUseStatus(requestDto);
    }

    public BigDecimal calculateDiscount(Coupon coupon, BigDecimal orderAmount) {
        if (orderAmount.compareTo(BigDecimal.valueOf(coupon.getMinOrderAmount())) < 0) {
            throw new BaseException(ErrorCode.COUPON_MIN_ORDER_AMOUNT_NOT_MET);
        }
        
        if (coupon.getDiscountType() == CouponDiscountType.FIXED_AMOUNT) {
            return BigDecimal.valueOf(coupon.getDiscountAmount());
        } else {
            BigDecimal discountRate = BigDecimal.valueOf(coupon.getDiscountAmount()).divide(BigDecimal.valueOf(100));
            return orderAmount.multiply(discountRate);
        }
    }

    public void validateCouponForUse(Coupon coupon, BigDecimal orderAmount) {
        coupon.validateForUse(orderAmount);
    }
}
