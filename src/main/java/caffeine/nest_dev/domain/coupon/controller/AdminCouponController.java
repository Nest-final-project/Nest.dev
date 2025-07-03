package caffeine.nest_dev.domain.coupon.controller;

import caffeine.nest_dev.common.dto.CommonResponse;
import caffeine.nest_dev.common.dto.PagingResponse;
import caffeine.nest_dev.common.enums.SuccessCode;
import caffeine.nest_dev.domain.coupon.dto.request.AdminCouponRequestDto;
import caffeine.nest_dev.domain.coupon.dto.response.AdminCouponResponseDto;
import caffeine.nest_dev.domain.coupon.service.AdminCouponService;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Admin Coupon", description = "관리자 쿠폰 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/coupons")
public class AdminCouponController {

    private final AdminCouponService adminCouponService;

    @Operation(summary = "쿠폰 등록", description = "관리자가 새로운 쿠폰을 등록합니다")
    @ApiResponse(responseCode = "201", description = "쿠폰 등록 성공")
    @PostMapping
    public ResponseEntity<CommonResponse<AdminCouponResponseDto>> registerCoupon(
            @Parameter(description = "쿠폰 등록 요청 정보") @RequestBody AdminCouponRequestDto requestDto
    ) {
        AdminCouponResponseDto responseDto = adminCouponService.saveCoupon(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.of(SuccessCode.SUCCESS_ADMIN_COUPON_CREATED, responseDto));
    }

    @Operation(summary = "쿠폰 목록 조회", description = "관리자가 등록된 쿠폰 목록을 조회합니다")
    @ApiResponse(responseCode = "200", description = "쿠폰 목록 조회 성공")
    @GetMapping
    public ResponseEntity<CommonResponse<PagingResponse<AdminCouponResponseDto>>> findCoupons(
            @Parameter(description = "페이지 정보") @PageableDefault(size = 10, sort = "validTo", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        PagingResponse<AdminCouponResponseDto> responseDtos = adminCouponService.getCoupon(pageable);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_ADMIN_COUPON_READ, responseDtos));
    }

    @Operation(summary = "쿠폰 수정", description = "관리자가 기존 쿠폰 정보를 수정합니다")
    @ApiResponse(responseCode = "200", description = "쿠폰 수정 성공")
    @PatchMapping("/{couponId}")
    public ResponseEntity<CommonResponse<Void>> updateCoupon(
            @Parameter(description = "수정할 쿠폰 ID") @PathVariable Long couponId,
            @Parameter(description = "쿠폰 수정 요청 정보") @RequestBody AdminCouponRequestDto requestDto
    ) {
        adminCouponService.modifyCoupon(couponId, requestDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_ADMIN_COUPON_UPDATED));
    }

    @Operation(summary = "쿠폰 삭제", description = "관리자가 쿠폰을 삭제합니다")
    @ApiResponse(responseCode = "200", description = "쿠폰 삭제 성공")
    @DeleteMapping("/{couponId}")
    public ResponseEntity<CommonResponse<Void>> deleteCoupon(
            @Parameter(description = "삭제할 쿠폰 ID") @PathVariable Long couponId) {
        adminCouponService.removeCoupon(couponId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_ADMIN_COUPON_DELETED));
    }
}
