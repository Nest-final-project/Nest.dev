package caffeine.nest_dev.domain.review.service;

import caffeine.nest_dev.common.dto.PagingResponse;
import caffeine.nest_dev.common.enums.ErrorCode;
import caffeine.nest_dev.common.exception.BaseException;
import caffeine.nest_dev.domain.review.dto.response.ReviewResponseDto;
import caffeine.nest_dev.domain.review.entity.Review;
import caffeine.nest_dev.domain.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminReviewService {
    private final ReviewRepository reviewRepository;

    @Transactional(readOnly = true)
    public PagingResponse<ReviewResponseDto> getReviewList(Long userId, Pageable pageable){
        Page<Review> reviews;

        if(userId != null){
            reviews = reviewRepository.findByMentorIdOrMenteeId(userId, userId, pageable);
        }else{
            reviews = reviewRepository.findAll(pageable);
        }


        Page<ReviewResponseDto> responseDtos = reviews.map(ReviewResponseDto::of);

        return PagingResponse.from(responseDtos);
    }

    @Transactional
    public Review changeReviewStatus(Long reviewId){
        Review review = reviewRepository.findById(reviewId).orElseThrow(()-> new BaseException(
                ErrorCode.REVIEW_NOT_FOUND));

        review.changeStatus();

        return review;
    }
}
