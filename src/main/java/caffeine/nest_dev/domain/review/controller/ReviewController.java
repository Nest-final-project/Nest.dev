package caffeine.nest_dev.domain.review.controller;

import caffeine.nest_dev.common.dto.CommonResponse;
import caffeine.nest_dev.common.dto.PagingResponse;
import caffeine.nest_dev.common.enums.SuccessCode;
import caffeine.nest_dev.domain.review.dto.request.ReviewRequestDto;
import caffeine.nest_dev.domain.review.dto.response.ReviewResponseDto;
import caffeine.nest_dev.domain.review.service.ReviewService;
import caffeine.nest_dev.domain.user.entity.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/reservations/{reservationId}/reviews")
    public ResponseEntity<CommonResponse<ReviewResponseDto>> save(@PathVariable Long reservationId,
            @RequestBody ReviewRequestDto reviewRequestDto,
            @AuthenticationPrincipal UserDetailsImpl authUser) {

        Long userId = authUser.getId();

        ReviewResponseDto reviewResponseDto = reviewService.save(reservationId, userId,
                reviewRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.of(SuccessCode.SUCCESS_CREATE_REVIEW, reviewResponseDto));
    }


    // 멘토별 리뷰 목록 조회
    @GetMapping("/mentors/{mentorId}/reviews")
    public ResponseEntity<CommonResponse<PagingResponse<ReviewResponseDto>>> getMentorReviews(
            @PathVariable Long mentorId, @PageableDefault(page = 0, size = 10) Pageable pageable) {

        Page<ReviewResponseDto> getMentorReviewList = reviewService.getMentorReviews(mentorId,
                pageable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_SHOW_REVIEWS,
                        PagingResponse.from(getMentorReviewList)));
    }

    @GetMapping("/reviews")
    public ResponseEntity<CommonResponse<PagingResponse<ReviewResponseDto>>> getMyReviews(
            @AuthenticationPrincipal UserDetailsImpl authUser,
            @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {

        Long userId = authUser.getId();

        Page<ReviewResponseDto> getMyReviewList = reviewService.getMyReviews(userId, pageable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_SHOW_REVIEWS, PagingResponse.from(getMyReviewList)));
    }

    @PatchMapping("/reviews/{reviewId}")
    public ResponseEntity<CommonResponse<Void>> update(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal UserDetailsImpl authUser,
            @RequestBody ReviewRequestDto reviewRequestDto
    ) {
        Long userId = authUser.getId();

        reviewService.update(userId, reviewId, reviewRequestDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_UPDATE_REVIEW));

    }

    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<CommonResponse<Void>> delete(@PathVariable Long reviewId) {

        reviewService.delete(reviewId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_DELETE_REVIEW));
    }

}
