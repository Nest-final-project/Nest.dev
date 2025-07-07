package caffeine.nest_dev.domain.review.controller;

import caffeine.nest_dev.common.dto.CommonResponse;
import caffeine.nest_dev.common.dto.PagingResponse;
import caffeine.nest_dev.common.enums.SuccessCode;
import caffeine.nest_dev.domain.review.dto.request.ReviewRequestDto;
import caffeine.nest_dev.domain.review.dto.response.ReviewResponseDto;
import caffeine.nest_dev.domain.review.service.ReviewService;
import caffeine.nest_dev.domain.user.entity.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Review", description = "리뷰 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "리뷰 작성", description = "예약에 대한 리뷰를 작성합니다")
    @ApiResponse(responseCode = "201", description = "리뷰 작성 성공")
    @PostMapping("/reservations/{reservationId}/reviews")
    public ResponseEntity<CommonResponse<ReviewResponseDto>> save(
            @Parameter(description = "예약 ID") @PathVariable Long reservationId,
            @Parameter(description = "리뷰 작성 요청 정보") @Valid @RequestBody ReviewRequestDto reviewRequestDto,
            @Parameter(description = "인증된 사용자 정보") @AuthenticationPrincipal UserDetailsImpl authUser) {

        Long userId = authUser.getId();

        ReviewResponseDto reviewResponseDto = reviewService.save(reservationId, userId,
                reviewRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.of(SuccessCode.SUCCESS_CREATE_REVIEW, reviewResponseDto));
    }


    // 멘토별 리뷰 목록 조회
    @Operation(summary = "멘토별 리뷰 조회", description = "특정 멘토의 리뷰 목록을 조회합니다")
    @ApiResponse(responseCode = "200", description = "멘토별 리뷰 조회 성공")
    @GetMapping("/mentors/{mentorId}/reviews")
    public ResponseEntity<CommonResponse<PagingResponse<ReviewResponseDto>>> getMentorReviews(
            @Parameter(description = "멘토 ID") @PathVariable Long mentorId, 
            @Parameter(description = "페이지 정보") @PageableDefault() Pageable pageable) {

        PagingResponse<ReviewResponseDto> getMentorReviewList = reviewService.getMentorReviews(
                mentorId,
                pageable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_SHOW_REVIEWS,
                        getMentorReviewList));
    }

    @Operation(summary = "내 리뷰 조회", description = "사용자가 작성한 리뷰 목록을 조회합니다")
    @ApiResponse(responseCode = "200", description = "내 리뷰 조회 성공")
    @GetMapping("/reviews")
    public ResponseEntity<CommonResponse<PagingResponse<ReviewResponseDto>>> getMyReviews(
            @Parameter(description = "인증된 사용자 정보") @AuthenticationPrincipal UserDetailsImpl authUser,
            @Parameter(description = "페이지 정보") @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {

        Long userId = authUser.getId();

        PagingResponse<ReviewResponseDto> getMyReviewList = reviewService.getMyReviews(userId,
                pageable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_SHOW_REVIEWS,
                        getMyReviewList));
    }

    @Operation(summary = "리뷰 수정", description = "기존 리뷰를 수정합니다")
    @ApiResponse(responseCode = "200", description = "리뷰 수정 성공")
    @PatchMapping("/reviews/{reviewId}")
    public ResponseEntity<CommonResponse<Void>> update(
            @Parameter(description = "수정할 리뷰 ID") @PathVariable Long reviewId,
            @Parameter(description = "인증된 사용자 정보") @AuthenticationPrincipal UserDetailsImpl authUser,
            @Parameter(description = "리뷰 수정 요청 정보") @Valid @RequestBody ReviewRequestDto reviewRequestDto
    ) {
        Long userId = authUser.getId();

        reviewService.update(userId, reviewId, reviewRequestDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_UPDATE_REVIEW));

    }

    @Operation(summary = "리뷰 삭제", description = "리뷰를 삭제합니다")
    @ApiResponse(responseCode = "200", description = "리뷰 삭제 성공")
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<CommonResponse<Void>> delete(
            @Parameter(description = "삭제할 리뷰 ID") @PathVariable Long reviewId) {

        reviewService.delete(reviewId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_DELETE_REVIEW));
    }

}
