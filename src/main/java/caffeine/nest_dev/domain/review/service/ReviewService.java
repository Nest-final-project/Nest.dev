package caffeine.nest_dev.domain.review.service;

import caffeine.nest_dev.common.enums.ErrorCode;
import caffeine.nest_dev.common.exception.BaseException;
import caffeine.nest_dev.domain.reservation.entity.Reservation;
import caffeine.nest_dev.domain.reservation.repository.ReservationRepository;
import caffeine.nest_dev.domain.review.dto.request.ReviewRequestDto;
import caffeine.nest_dev.domain.review.dto.response.ReviewResponseDto;
import caffeine.nest_dev.domain.review.entity.Review;
import caffeine.nest_dev.domain.review.repository.ReviewRespository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRespository reviewRespository;
    private final ReservationRepository reservationRepository;

    @Transactional
    public ReviewResponseDto save(Long reservationId, Long userId,
            ReviewRequestDto reviewRequestDto) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BaseException(ErrorCode.RESERVATION_NOT_FOUND));

        if (!reservation.getMentee().getId().equals(userId)) {
            throw new BaseException(ErrorCode.ACCESS_DENIED);
        }

        reviewRespository.findByReservationId(reservationId).ifPresent(r -> {
            throw new BaseException(ErrorCode.REVIEW_ALREADY_EXISTS);
        });

        Review review = reviewRespository.save(reviewRequestDto.toEntity());

        return ReviewResponseDto.of(review);

    }

    // 멘토별 리뷰 목록 조회
    @Transactional(readOnly = true)
    public Page<ReviewResponseDto> getMentorReviews(Long mentorId, Pageable pageable) {

        Page<Review> getMentorReviewList = reviewRespository.findByMentorId(mentorId, pageable);

        return getMentorReviewList.map(ReviewResponseDto::of);
    }

    // 내가 작성한 리뷰 목록 조회
    @Transactional(readOnly = true)
    public Page<ReviewResponseDto> getMyReviews(Long mentee, Pageable pageable) {

        Page<Review> getMyReviewList = reviewRespository.findByMenteeId(mentee, pageable);

        return getMyReviewList.map(ReviewResponseDto::of);
    }

    @Transactional
    public ReviewResponseDto update(Long userId, Long reviewId, ReviewRequestDto reviewRequestDto) {
        Review review = reviewRespository.findById(reviewId)
                .orElseThrow(() -> new BaseException(ErrorCode.REVIEW_NOT_FOUND));

        Reservation reservation = reservationRepository.findById(review.getReservation().getId())
                .orElseThrow(() -> new BaseException(ErrorCode.RESERVATION_NOT_FOUND));

        if (!reservation.getMentee().getId().equals(userId)) {
            throw new BaseException(ErrorCode.ACCESS_DENIED);
        }

        review.update(reviewRequestDto);

        return ReviewResponseDto.of(review);
    }

    @Transactional
    public void delete(Long reviewId) {
        Review review = reviewRespository.findById(reviewId)
                .orElseThrow(() -> new BaseException(ErrorCode.REVIEW_NOT_FOUND));

        reviewRespository.delete(review);
    }
}
