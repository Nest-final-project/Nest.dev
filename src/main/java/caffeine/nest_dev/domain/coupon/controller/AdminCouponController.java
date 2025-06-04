package caffeine.nest_dev.domain.coupon.controller;

import caffeine.nest_dev.common.dto.CommonResponse;
import caffeine.nest_dev.common.dto.PagingResponse;
import caffeine.nest_dev.common.enums.SuccessCode;
import caffeine.nest_dev.domain.coupon.dto.request.AdminCouponRequestDto;
import caffeine.nest_dev.domain.coupon.dto.response.AdminCouponResponseDto;
import caffeine.nest_dev.domain.coupon.service.AdminCouponService;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/coupons")
public class AdminCouponController {

    private final AdminCouponService adminCouponService;

    @PostMapping
    public ResponseEntity<CommonResponse<AdminCouponResponseDto>> registerCoupon(
            @RequestBody AdminCouponRequestDto requestDto
    ) {
        AdminCouponResponseDto responseDto = adminCouponService.saveCoupon(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.of(SuccessCode.SUCCESS_ADMIN_COUPON_CREATED, responseDto));
    }

    @GetMapping
    public ResponseEntity<CommonResponse<PagingResponse<AdminCouponResponseDto>>> findCoupons(
            @PageableDefault(size = 10, sort = "validTo", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        PagingResponse<AdminCouponResponseDto> responseDtos = adminCouponService.getCoupon(pageable);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_TICKET_READ, responseDtos));
    }

    @PatchMapping("/{couponId}")
    public ResponseEntity<CommonResponse<AdminCouponResponseDto>> updateCoupon(
            @PathVariable Long couponId,
            @RequestBody AdminCouponRequestDto requestDto
    ) {
        adminCouponService.modifyCoupon(couponId, requestDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_ADMIN_COUPON_UPDATED));
    }

    @DeleteMapping("/{couponId}")
    public ResponseEntity<CommonResponse> deleteCoupon(
            @PathVariable Long couponId) {
        adminCouponService.removeCoupon(couponId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_ADMIN_COUPON_DELETED));
    }
}
