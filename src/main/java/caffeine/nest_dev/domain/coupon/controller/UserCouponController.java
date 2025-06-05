package caffeine.nest_dev.domain.coupon.controller;

import caffeine.nest_dev.common.dto.CommonResponse;
import caffeine.nest_dev.common.dto.PagingResponse;
import caffeine.nest_dev.common.enums.SuccessCode;
import caffeine.nest_dev.domain.coupon.dto.request.UserCouponRequestDto;
import caffeine.nest_dev.domain.coupon.dto.response.UserCouponResponseDto;
import caffeine.nest_dev.domain.coupon.service.UserCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user-coupons")
public class UserCouponController {

    private final UserCouponService userCouponService;

    @PostMapping
    public ResponseEntity<CommonResponse<UserCouponResponseDto>> registerUserCoupon(
            @RequestBody UserCouponRequestDto requestDto) {
        UserCouponResponseDto responseDto = userCouponService.saveUserCoupon(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.of(SuccessCode.SUCCESS_USER_COUPON_CREATED, responseDto));
    }

    @GetMapping
    public ResponseEntity<CommonResponse<PagingResponse<UserCouponResponseDto>>> findUserCoupons(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        PagingResponse<UserCouponResponseDto> responseDtos = userCouponService.getUserCoupon(pageable);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_USER_COUPON_READ, responseDtos));
    }

    @PatchMapping("/use")
    public ResponseEntity<CommonResponse<Void>> updateUseUserCoupon(
            @RequestBody UserCouponRequestDto requestDto
    ) {
        userCouponService.modifyUseCoupon(requestDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_USER_COUPON_UPDATED));
    }
}
