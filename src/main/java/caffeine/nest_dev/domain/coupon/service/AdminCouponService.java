package caffeine.nest_dev.domain.coupon.service;

import caffeine.nest_dev.common.dto.PagingResponse;
import caffeine.nest_dev.common.enums.ErrorCode;
import caffeine.nest_dev.common.exception.BaseException;
import caffeine.nest_dev.domain.coupon.dto.request.AdminCouponRequestDto;
import caffeine.nest_dev.domain.coupon.dto.response.AdminCouponResponseDto;
import caffeine.nest_dev.domain.coupon.entity.Coupon;
import caffeine.nest_dev.domain.coupon.repository.AdminCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminCouponService {

    private final AdminCouponRepository adminCouponRepository;

    @Transactional
    public AdminCouponResponseDto saveCoupon(AdminCouponRequestDto requestDto) {
        Coupon coupon = adminCouponRepository.save(requestDto.toEntity());
        return AdminCouponResponseDto.of(coupon);
    }

    @Transactional(readOnly = true)
    public PagingResponse<AdminCouponResponseDto> getCoupon(Pageable pageable) {
        Page<Coupon> pagingResponse = adminCouponRepository.findAll(pageable);
        Page<AdminCouponResponseDto> responseDtos = pagingResponse.map(AdminCouponResponseDto::of);
        return PagingResponse.from(responseDtos);
    }

    @Transactional
    public void modifyCoupon(Long couponId, AdminCouponRequestDto requestDto) {
        Coupon coupon = findAdminCouponById(couponId);
        coupon.modifyCoupon(requestDto);
    }

    @Transactional
    public void removeCoupon(Long couponId) {
        Coupon coupon = findAdminCouponById(couponId);
        adminCouponRepository.delete(coupon);
    }

    private Coupon findAdminCouponById(Long couponId) {
        return adminCouponRepository.findById(couponId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_ADMIN_COUPON));
    }

}
