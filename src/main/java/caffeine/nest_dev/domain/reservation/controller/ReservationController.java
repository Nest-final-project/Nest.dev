package caffeine.nest_dev.domain.reservation.controller;

import caffeine.nest_dev.common.dto.CommonResponse;
import caffeine.nest_dev.common.dto.PagingResponse;
import caffeine.nest_dev.common.enums.SuccessCode;
import caffeine.nest_dev.domain.reservation.dto.request.ReservationCancelRequestDto;
import caffeine.nest_dev.domain.reservation.dto.request.ReservationRequestDto;
import caffeine.nest_dev.domain.reservation.dto.response.ReservationResponseDto;
import caffeine.nest_dev.domain.reservation.service.ReservationService;
import caffeine.nest_dev.domain.user.entity.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/reservations")
    public ResponseEntity<CommonResponse<ReservationResponseDto>> save(@RequestBody
            ReservationRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl authUser) {

        Long userId = authUser.getId();

        ReservationResponseDto responseDto = reservationService.save(userId, requestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.of(SuccessCode.SUCCESS_CREATE_RESERVATION, responseDto));
    }

    @GetMapping("/reservations")
    public ResponseEntity<CommonResponse<PagingResponse<ReservationResponseDto>>> getReservationList(
            @AuthenticationPrincipal UserDetailsImpl authUser,
            @PageableDefault() Pageable pageable) {

        PagingResponse<ReservationResponseDto> getReservationList = reservationService.getReservationList(
                authUser.getId(), pageable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_SHOW_RESERVATION_LIST,
                        getReservationList));

    }

    @GetMapping("/reservations/{reservationId}")
    public ResponseEntity<CommonResponse<ReservationResponseDto>> getReservation(
            @PathVariable Long reservationId, @AuthenticationPrincipal UserDetailsImpl authUser) {

        Long userId = authUser.getId();

        ReservationResponseDto reservation = reservationService.getReservation(reservationId, userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_SHOW_RESERVATION, reservation));
    }

    @DeleteMapping("/reservations/{reservationId}")
    public ResponseEntity<CommonResponse<Void>> menteeCancel(@PathVariable Long reservationId,
            @AuthenticationPrincipal UserDetailsImpl authUser,
            @RequestBody ReservationCancelRequestDto cancelRequestDto) {

        reservationService.update(authUser, reservationId, cancelRequestDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_CANCEL_RESERVATION));

    }

    @DeleteMapping("mentor/reservations/{reservationId}")
    public ResponseEntity<CommonResponse<Void>> mentorCancel(@PathVariable Long reservationId,
            @AuthenticationPrincipal UserDetailsImpl authUser,
            @RequestBody ReservationCancelRequestDto cancelRequestDto) {

        reservationService.update(authUser, reservationId, cancelRequestDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_CANCEL_RESERVATION));

    }

}
