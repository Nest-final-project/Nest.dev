package caffeine.nest_dev.domain.reservation.service;

import caffeine.nest_dev.common.dto.PagingResponse;
import caffeine.nest_dev.common.enums.ErrorCode;
import caffeine.nest_dev.common.exception.BaseException;
import caffeine.nest_dev.domain.reservation.dto.request.ReservationCancelRequestDto;
import caffeine.nest_dev.domain.reservation.dto.request.ReservationRequestDto;
import caffeine.nest_dev.domain.reservation.dto.response.ReservationResponseDto;
import caffeine.nest_dev.domain.reservation.entity.Reservation;
import caffeine.nest_dev.domain.reservation.enums.ReservationStatus;
import caffeine.nest_dev.domain.reservation.lock.DistributedLock;
import caffeine.nest_dev.domain.reservation.repository.ReservationRepository;
import caffeine.nest_dev.domain.ticket.entity.Ticket;
import caffeine.nest_dev.domain.ticket.repository.TicketRepository;
import caffeine.nest_dev.domain.user.entity.User;
import caffeine.nest_dev.domain.user.entity.UserDetailsImpl;
import caffeine.nest_dev.domain.user.enums.UserRole;
import caffeine.nest_dev.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserService userService;
    private final TicketRepository ticketRepository;

    @DistributedLock(key = "'reserve:' + #requestDto.mentor")
    public ReservationResponseDto save(Long userId, ReservationRequestDto requestDto) {

        // 멘토 중복 체크
        boolean mentor_exists = reservationRepository.existsByMentorTime(
                requestDto.getMentor(), requestDto.getReservationStartAt(),
                requestDto.getReservationEndAt(), ReservationStatus.CANCELED
        );

        if (mentor_exists) {
            throw new BaseException(ErrorCode.DUPLICATED_RESERVATION);
        }

        // 멘티 중복 체크
        boolean mentee_exists = reservationRepository.existsByMenteeTime(
                userId, requestDto.getReservationStartAt(),
                requestDto.getReservationEndAt(), ReservationStatus.CANCELED);

        if (mentee_exists) {
            throw new BaseException(ErrorCode.DUPLICATED_RESERVATION);
        }

        User mentor = userService.findByIdAndIsDeletedFalseOrElseThrow(requestDto.getMentor());

        User mentee = userService.findByIdAndIsDeletedFalseOrElseThrow(userId);

        Ticket ticket = ticketRepository.findById(requestDto.getTicket())
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_TICKET));

        Reservation reservation = reservationRepository.save(
                requestDto.toEntity(mentor, mentee, ticket));

        return ReservationResponseDto.of(reservation);

    }

    @Transactional(readOnly = true)
    public PagingResponse<ReservationResponseDto> getReservationList(Long userId,
            Pageable pageable) {
        // MENTEE 경우
        User user = userService.findByIdAndIsDeletedFalseOrElseThrow(userId);

        if (user.getUserRole().equals(UserRole.MENTEE)) {
            Page<Reservation> reservationPage = reservationRepository.findByMentorIdOrMenteeId(
                    userId,
                    userId, pageable);
            Page<ReservationResponseDto> responseDtos = reservationPage.map(
                    ReservationResponseDto::of);

            return PagingResponse.from(responseDtos);
        }

        // MENTOR 경우
        Page<Reservation> reservationPage = reservationRepository.findByMentorIdAndReservationStatus(
                userId,
                ReservationStatus.PAID, pageable);
        Page<ReservationResponseDto> responseDtos = reservationPage.map(ReservationResponseDto::of);

        return PagingResponse.from(responseDtos);

    }

    @SuppressWarnings("checkstyle:OperatorWrap")
    @Transactional(readOnly = true)
    public ReservationResponseDto getReservation(Long reservationId, Long userId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BaseException(ErrorCode.RESERVATION_NOT_FOUND));

        if (!reservation.getMentee().getId().equals(userId) &&
                !reservation.getMentor().getId().equals(userId)) {
            throw new BaseException(ErrorCode.NO_PERMISSION);
        }

        return ReservationResponseDto.of(reservation);
    }

    public void deleteReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BaseException(ErrorCode.RESERVATION_NOT_FOUND));

        if(!reservation.getReservationStatus().equals(ReservationStatus.REQUESTED)) {
            throw new BaseException(ErrorCode.ONLY_REQUESTED_CAN_BE_CANCELED);
        }
        reservationRepository.delete(reservation);
    }

}
