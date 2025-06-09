package caffeine.nest_dev.domain.review.controller;

import caffeine.nest_dev.common.dto.CommonResponse;
import caffeine.nest_dev.common.dto.PagingResponse;
import caffeine.nest_dev.common.enums.SuccessCode;
import caffeine.nest_dev.domain.review.dto.response.AdminReviewResponseDto;
import caffeine.nest_dev.domain.review.dto.response.ReviewResponseDto;
import caffeine.nest_dev.domain.review.entity.Review;
import caffeine.nest_dev.domain.review.enums.ReviewStatus;
import caffeine.nest_dev.domain.review.service.AdminReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminReviewController {

    private final AdminReviewService adminReviewService;

    @GetMapping("/reviews")
    public ResponseEntity<CommonResponse<PagingResponse<ReviewResponseDto>>> getReviewList(
            @RequestParam(required = false) Long userId, @PageableDefault Pageable pageable) {

        PagingResponse<ReviewResponseDto> getReviewList = adminReviewService.getReviewList(userId,
                pageable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_SHOW_REVIEWS, getReviewList));
    }

    @PatchMapping("/reviews/{reviewId}")
    public ResponseEntity<CommonResponse<AdminReviewResponseDto>> changeReviewStatus(
            @PathVariable Long reviewId) {

        Review review = adminReviewService.changeReviewStatus(reviewId);

        if(review.getReviewStatus().equals(ReviewStatus.DELETED)){
            return ResponseEntity.status(HttpStatus.OK)
                    .body(CommonResponse.of(SuccessCode.SUCCESS_DELETE_REVIEW));
        }else{
            return ResponseEntity.status(HttpStatus.OK)
                    .body(CommonResponse.of(SuccessCode.SUCCESS_ACTIVE_REVIEW));
        }
    }
}
