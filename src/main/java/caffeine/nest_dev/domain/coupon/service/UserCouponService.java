package caffeine.nest_dev.domain.coupon.service;

import caffeine.nest_dev.common.dto.PagingResponse;
import caffeine.nest_dev.common.enums.ErrorCode;
import caffeine.nest_dev.common.exception.BaseException;
import caffeine.nest_dev.domain.coupon.dto.request.UserCouponRequestDto;
import caffeine.nest_dev.domain.coupon.dto.response.AdminCouponResponseDto;
import caffeine.nest_dev.domain.coupon.dto.response.UserCouponResponseDto;
import caffeine.nest_dev.domain.coupon.entity.Coupon;
import caffeine.nest_dev.domain.coupon.entity.UserCoupon;
import caffeine.nest_dev.domain.coupon.entity.UserCouponId;
import caffeine.nest_dev.domain.coupon.enums.CouponDiscountType;
import caffeine.nest_dev.domain.coupon.repository.AdminCouponRepository;
import caffeine.nest_dev.domain.coupon.repository.UserCouponRepository;
import caffeine.nest_dev.domain.reservation.lock.DistributedLock;
import caffeine.nest_dev.domain.user.entity.User;
import caffeine.nest_dev.domain.user.entity.UserDetailsImpl;
import caffeine.nest_dev.domain.user.enums.UserGrade;
import caffeine.nest_dev.domain.user.service.UserService;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserCouponService {

    private final UserCouponRepository userCouponRepository;
    private final AdminCouponRepository adminCouponRepository;
    private final UserService userService;

    @DistributedLock(key = "'coupon:' + #requestDto.couponId")
    public UserCouponResponseDto saveUserCoupon(UserCouponRequestDto requestDto) {
        Coupon coupon = adminCouponRepository.findById(requestDto.getCouponId())
                .orElseThrow(() -> new BaseException(
                        ErrorCode.NOT_FOUND_USER_COUPON));
        User user = userService.findByIdAndIsDeletedFalseOrElseThrow(requestDto.getUserId());

        // 중복 발급 방지 검증
        UserCouponId userCouponId = new UserCouponId(requestDto.getCouponId(),
                requestDto.getUserId());
        if (userCouponRepository.existsById(userCouponId)) {
            throw new BaseException(ErrorCode.COUPON_ALREADY_ISSUED);
        }

        coupon.issue();
        UserCoupon userCoupon = requestDto.toEntity(coupon, user);
        userCouponRepository.save(userCoupon);
        return UserCouponResponseDto.of(userCoupon);
    }

    @Transactional(readOnly = true)
    public PagingResponse<UserCouponResponseDto> getUserCoupon(Pageable pageable,
            UserDetailsImpl userDetails) {
        // 쿠폰 정보를 함께 조회하여 N+1 문제 해결
        Page<UserCoupon> pagingResponse = userCouponRepository.findByUserIdWithCoupon(
                userDetails.getId(), pageable);
        Page<UserCouponResponseDto> responseDtos = pagingResponse.map(UserCouponResponseDto::of);
        return PagingResponse.from(responseDtos);
    }

    @Transactional(readOnly = true)
    public PagingResponse<AdminCouponResponseDto> getUserAvailableCoupon(Pageable pageable,
            UserDetailsImpl userDetails) {
        // 로그인된 유저의 등급 찾기
        User user = userService.findByIdAndIsDeletedFalseOrElseThrow(userDetails.getId());

        UserGrade userGrade = user.getUserGrade();

        // 등급에 맞는 발급 가능한 쿠폰 찾기
        Page<Coupon> coupons = adminCouponRepository.findByMinGrade(userGrade, pageable);

        return PagingResponse.from(coupons.map(AdminCouponResponseDto::of));
    }

    @Transactional
    public void modifyUserCoupon(UserCouponRequestDto requestDto) {
        UserCouponId userCouponId = new UserCouponId(requestDto.getCouponId(),
                requestDto.getUserId());
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
            BigDecimal discountRate = BigDecimal.valueOf(coupon.getDiscountAmount())
                    .divide(BigDecimal.valueOf(100));
            return orderAmount.multiply(discountRate);
        }
    }

    public void validateCouponForUse(Coupon coupon, BigDecimal orderAmount) {
        coupon.validateForUse(orderAmount);
    }
}
