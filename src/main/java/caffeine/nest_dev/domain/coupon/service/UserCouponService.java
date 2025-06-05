package caffeine.nest_dev.domain.coupon.service;

import caffeine.nest_dev.common.dto.PagingResponse;
import caffeine.nest_dev.common.enums.ErrorCode;
import caffeine.nest_dev.common.exception.BaseException;
import caffeine.nest_dev.domain.coupon.dto.request.UserCouponRequestDto;
import caffeine.nest_dev.domain.coupon.dto.response.UserCouponResponseDto;
import caffeine.nest_dev.domain.coupon.entity.Coupon;
import caffeine.nest_dev.domain.coupon.entity.UserCoupon;
import caffeine.nest_dev.domain.coupon.entity.UserCouponId;
import caffeine.nest_dev.domain.coupon.repository.AdminCouponRepository;
import caffeine.nest_dev.domain.coupon.repository.UserCouponRepository;
import caffeine.nest_dev.domain.user.entity.User;
import caffeine.nest_dev.domain.user.repository.UserRepository;
import caffeine.nest_dev.domain.user.service.UserService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCouponService {

    private final UserCouponRepository userCouponRepository;
    private final AdminCouponRepository adminCouponRepository;
    private final UserService userService;

    @Transactional
    public UserCouponResponseDto saveUserCoupon(UserCouponRequestDto requestDto) {
        Coupon coupon = adminCouponRepository.findById(requestDto.getCouponId())
                .orElseThrow(() -> new BaseException(
                        ErrorCode.NOT_FOUND_USER_COUPON));
        User user = userService.findByIdAndIsDeletedFalseOrElseThrow(requestDto.getUserId());
        coupon.issue();
        UserCoupon userCoupon = userCouponRepository.save(requestDto.toEntity(coupon, user));
        return UserCouponResponseDto.of(userCoupon);
    }

    @Transactional(readOnly = true)
    public PagingResponse<UserCouponResponseDto> getUserCoupon(Pageable pageable) {
        Page<UserCoupon> pagingResponse = userCouponRepository.findAll(pageable);
        Page<UserCouponResponseDto> responseDtos = pagingResponse.map(UserCouponResponseDto::of);
        return PagingResponse.from(responseDtos);
    }

    @Transactional
    public void modifyUseCoupon(UserCouponRequestDto requestDto) {
        UserCouponId userCouponId = new UserCouponId(requestDto.getCouponId(), requestDto.getUserId());
        UserCoupon userCoupon = userCouponRepository.findById(userCouponId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_USER_COUPON));
        userCoupon.modifyUseStatus(requestDto);
    }
}
