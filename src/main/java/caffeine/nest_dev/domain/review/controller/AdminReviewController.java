package caffeine.nest_dev.domain.review.controller;

import caffeine.nest_dev.common.dto.CommonResponse;
import caffeine.nest_dev.common.dto.PagingResponse;
import caffeine.nest_dev.common.enums.SuccessCode;
import caffeine.nest_dev.domain.review.dto.response.AdminReviewResponseDto;
import caffeine.nest_dev.domain.review.dto.response.ReviewResponseDto;
import caffeine.nest_dev.domain.review.entity.Review;
import caffeine.nest_dev.domain.review.enums.ReviewStatus;
import caffeine.nest_dev.domain.review.service.AdminReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Admin Review", description = "관리자 리뷰 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminReviewController {

    private final AdminReviewService adminReviewService;

    @Operation(summary = "관리자 리뷰 목록 조회", description = "관리자가 리뷰 목록을 조회합니다")
    @ApiResponse(responseCode = "200", description = "관리자 리뷰 목록 조회 성공")
    @GetMapping("/reviews")
    public ResponseEntity<CommonResponse<PagingResponse<ReviewResponseDto>>> getReviewList(
            @Parameter(description = "필터링할 사용자 ID") @RequestParam(required = false) Long userId, 
            @Parameter(description = "페이지 정보") @PageableDefault Pageable pageable) {

        PagingResponse<ReviewResponseDto> getReviewList = adminReviewService.getReviewList(userId,
                pageable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_SHOW_REVIEWS, getReviewList));
    }

    @Operation(summary = "리뷰 상태 변경", description = "관리자가 리뷰의 상태를 변경합니다 (활성/삭제)")
    @ApiResponse(responseCode = "200", description = "리뷰 상태 변경 성공")
    @PatchMapping("/reviews/{reviewId}")
    public ResponseEntity<CommonResponse<AdminReviewResponseDto>> changeReviewStatus(
            @Parameter(description = "상태를 변경할 리뷰 ID") @PathVariable Long reviewId) {

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
