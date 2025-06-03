package caffeine.nest_dev.domain.coupon.service;

import caffeine.nest_dev.common.dto.PagingResponse;
import caffeine.nest_dev.domain.coupon.dto.request.AdminCouponRequestDto;
import caffeine.nest_dev.domain.coupon.dto.response.AdminCouponResponseDto;
import caffeine.nest_dev.domain.coupon.entity.Coupon;
import caffeine.nest_dev.domain.coupon.repository.AdminCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
}
