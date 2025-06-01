package caffeine.nest_dev.domain.review.controller;

import caffeine.nest_dev.common.dto.CommonResponse;
import caffeine.nest_dev.common.enums.SuccessCode;
import caffeine.nest_dev.domain.review.dto.request.ReviewRequestDto;
import caffeine.nest_dev.domain.review.dto.response.ReviewResponseDto;
import caffeine.nest_dev.domain.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<CommonResponse<ReviewResponseDto>> save
            (@PathVariable Long reservationId, @RequestBody ReviewRequestDto reviewRequestDto,
            @AuthenticationPrincipal AuthenticationPrincipal authUser
            ){

        ReviewResponseDto reviewResponseDto =
                reviewService.save(reservationId, authUser, reviewRequestDto);


        return ResponseEntity.ok(CommonResponse.of(SuccessCode.SUCCESS_CREATE_REIVEW, reviewResponseDto));
    }

}
