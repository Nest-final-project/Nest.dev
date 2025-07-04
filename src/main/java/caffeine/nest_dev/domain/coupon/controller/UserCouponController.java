package caffeine.nest_dev.domain.coupon.controller;

import caffeine.nest_dev.common.dto.CommonResponse;
import caffeine.nest_dev.common.dto.PagingResponse;
import caffeine.nest_dev.common.enums.SuccessCode;
import caffeine.nest_dev.domain.coupon.dto.request.UserCouponRequestDto;
import caffeine.nest_dev.domain.coupon.dto.response.UserCouponResponseDto;
import caffeine.nest_dev.domain.coupon.service.UserCouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User Coupon", description = "사용자 쿠폰 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user-coupons")
public class UserCouponController {

    private final UserCouponService userCouponService;

    @Operation(summary = "사용자 쿠폰 등록", description = "사용자가 쿠폰을 등록합니다")
    @ApiResponse(responseCode = "201", description = "사용자 쿠폰 등록 성공")
    @PostMapping
    public ResponseEntity<CommonResponse<UserCouponResponseDto>> registerUserCoupon(
            @Parameter(description = "사용자 쿠폰 등록 요청 정보") @RequestBody UserCouponRequestDto requestDto) {
        UserCouponResponseDto responseDto = userCouponService.saveUserCoupon(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.of(SuccessCode.SUCCESS_USER_COUPON_CREATED, responseDto));
    }

    @Operation(summary = "사용자 쿠폰 목록 조회", description = "사용자의 쿠폰 목록을 조회합니다")
    @ApiResponse(responseCode = "200", description = "사용자 쿠폰 목록 조회 성공")
    @GetMapping
    public ResponseEntity<CommonResponse<PagingResponse<UserCouponResponseDto>>> findUserCoupons(
            @Parameter(description = "페이지 정보") @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        PagingResponse<UserCouponResponseDto> responseDtos = userCouponService.getUserCoupon(pageable);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_USER_COUPON_READ, responseDtos));
    }

    @Operation(summary = "쿠폰 사용", description = "사용자가 보유한 쿠폰을 사용합니다")
    @ApiResponse(responseCode = "200", description = "쿠폰 사용 성공")
    @PatchMapping("/use")
    public ResponseEntity<CommonResponse<Void>> updateUseUserCoupon(
            @Parameter(description = "쿠폰 사용 요청 정보") @RequestBody UserCouponRequestDto requestDto
    ) {
        userCouponService.modifyUserCoupon(requestDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_USER_COUPON_UPDATED));
    }
}
