package caffeine.nest_dev.domain.review.service;

import caffeine.nest_dev.common.enums.ErrorCode;
import caffeine.nest_dev.common.exception.BaseException;
import caffeine.nest_dev.domain.reservation.entity.Reservation;
import caffeine.nest_dev.domain.reservation.repository.ReservationRepository;
import caffeine.nest_dev.domain.review.dto.request.ReviewRequestDto;
import caffeine.nest_dev.domain.review.dto.response.ReviewResponseDto;
import caffeine.nest_dev.domain.review.entity.Review;
import caffeine.nest_dev.domain.review.repository.ReviewRespository;
import caffeine.nest_dev.domain.user.entity.User;
import caffeine.nest_dev.domain.user.repository.UserRepository;
import ch.qos.logback.core.spi.ErrorCodes;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final UserRepository userRepository;
    private final ReviewRespository reviewRespository;
    private final ReservationRepository  reservationRepository;

    @Transactional
    public ReviewResponseDto save(Long reservationId, AuthenticationPrincipal authUser,
                                    ReviewRequestDto reviewRequestDto){
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(()-> new BaseException(ErrorCode.RESERVATION_NOT_FOUND));

        if (!reservation.getMentee().getId().equals(authUser.getUser().getId())) {
            throw new BaseException(ErrorCode.ACCESS_DENIED);
        }

        reviewRespository.findByReservationId(reservationId)
                .ifPresent(r ->{ throw new BaseException(ErrorCode.REVIEW_ALREADY_EXISTS);});

        Review review = Review.builder()
                .reservation(reservation)
                .mentor(reservation.getMentor())
                .mentee(reservation.getMentee())
                .content(reviewRequestDto.getContent())
                .build();

        reviewRespository.save(review);

        return new ReviewResponseDto(
                review.getId(),
                reservation.getId(),
                reservation.getMentor(),
                reservation.getMentee(),
                review.getContent());

    }

}
