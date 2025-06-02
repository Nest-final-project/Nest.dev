package caffeine.nest_dev.domain.coupon.repository;

import caffeine.nest_dev.common.dto.PagingResponse;
import caffeine.nest_dev.domain.coupon.dto.response.AdminCouponResponseDto;
import caffeine.nest_dev.domain.coupon.entity.Coupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.web.PageableDefault;

public interface AdminCouponRepository extends JpaRepository<Coupon, Long> {

    PagingResponse<AdminCouponResponseDto> findAll(PageableDefault pageable);
}
