package caffeine.nest_dev.domain.review.service;

import caffeine.nest_dev.common.dto.PagingResponse;
import caffeine.nest_dev.common.enums.ErrorCode;
import caffeine.nest_dev.common.exception.BaseException;
import caffeine.nest_dev.domain.reservation.entity.Reservation;
import caffeine.nest_dev.domain.reservation.enums.ReservationStatus;
import caffeine.nest_dev.domain.reservation.repository.ReservationRepository;
import caffeine.nest_dev.domain.review.dto.request.ReviewRequestDto;
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
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;

    @Transactional
    public ReviewResponseDto save(Long reservationId, Long userId,
            ReviewRequestDto reviewRequestDto) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BaseException(ErrorCode.RESERVATION_NOT_FOUND));

        if (!reservation.getMentee().getId().equals(userId)) {
            throw new BaseException(ErrorCode.ACCESS_DENIED);
        }

        if(!reservation.getReservationStatus().equals(ReservationStatus.COMPLETED)){
            throw new BaseException(ErrorCode.RESERVATION_NOT_COMPLETED);
        }

        reviewRepository.findByReservationId(reservationId).ifPresent(r -> {
            throw new BaseException(ErrorCode.REVIEW_ALREADY_EXISTS);
        });

        Review review = reviewRepository.save(
                reviewRequestDto.toEntity(reservation.getMentor(), reservation.getMentee(),
                        reservation));

        return ReviewResponseDto.of(review);

    }

    // 멘토별 리뷰 목록 조회
    @Transactional(readOnly = true)
    public PagingResponse<ReviewResponseDto> getMentorReviews(Long mentorId, Pageable pageable) {

        Page<Review> reviewPage = reviewRepository.findByMentorId(mentorId, pageable);
        Page<ReviewResponseDto> responseDtos = reviewPage.map(ReviewResponseDto::of);

        return PagingResponse.from(responseDtos);
    }

    // 내가 작성한 리뷰 목록 조회
    @Transactional(readOnly = true)
    public PagingResponse<ReviewResponseDto> getMyReviews(Long mentee, Pageable pageable) {

        Page<Review> reviews = reviewRepository.findByMenteeId(mentee, pageable);
        Page<ReviewResponseDto> responseDtos = reviews.map(ReviewResponseDto::of);

        return PagingResponse.from(responseDtos);
    }

    @Transactional
    public void update(Long userId, Long reviewId, ReviewRequestDto reviewRequestDto) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BaseException(ErrorCode.REVIEW_NOT_FOUND));

        Reservation reservation = reservationRepository.findById(review.getReservation().getId())
                .orElseThrow(() -> new BaseException(ErrorCode.RESERVATION_NOT_FOUND));

        if (!reservation.getMentee().getId().equals(userId)) {
            throw new BaseException(ErrorCode.ACCESS_DENIED);
        }

        review.update(reviewRequestDto);
    }

    @Transactional
    public void delete(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BaseException(ErrorCode.REVIEW_NOT_FOUND));

        review.softDelete();
    }
}
