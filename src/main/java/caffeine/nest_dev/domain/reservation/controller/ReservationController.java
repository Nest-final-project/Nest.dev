package caffeine.nest_dev.domain.reservation.controller;

import caffeine.nest_dev.common.dto.CommonResponse;
import caffeine.nest_dev.common.dto.PagingResponse;
import caffeine.nest_dev.common.enums.SuccessCode;
import caffeine.nest_dev.domain.reservation.dto.request.ReservationRequestDto;
import caffeine.nest_dev.domain.reservation.dto.response.ReservationResponseDto;
import caffeine.nest_dev.domain.reservation.service.ReservationService;
import caffeine.nest_dev.domain.user.entity.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Reservation", description = "예약 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReservationController {

    private final ReservationService reservationService;

    @Operation(summary = "예약 생성", description = "새로운 상담 예약을 생성합니다")
    @ApiResponse(responseCode = "201", description = "예약 생성 성공")
    @PostMapping("/reservations")
    public ResponseEntity<CommonResponse<ReservationResponseDto>> save(
            @Parameter(description = "예약 생성 요청 정보") @RequestBody ReservationRequestDto requestDto,
            @Parameter(description = "인증된 사용자 정보") @AuthenticationPrincipal UserDetailsImpl authUser) {

        Long userId = authUser.getId();

        ReservationResponseDto responseDto = reservationService.save(userId, requestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.of(SuccessCode.SUCCESS_CREATE_RESERVATION, responseDto));
    }

    @Operation(summary = "예약 목록 조회", description = "사용자의 예약 목록을 페이징하여 조회합니다")
    @ApiResponse(responseCode = "200", description = "예약 목록 조회 성공")
    @GetMapping("/reservations")
    public ResponseEntity<CommonResponse<PagingResponse<ReservationResponseDto>>> getReservationList(
            @Parameter(description = "인증된 사용자 정보") @AuthenticationPrincipal UserDetailsImpl authUser,
            @Parameter(description = "페이지 정보") @PageableDefault() Pageable pageable) {

        PagingResponse<ReservationResponseDto> getReservationList = reservationService.getReservationList(
                authUser.getId(), pageable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_SHOW_RESERVATION_LIST,
                        getReservationList));

    }

    @Operation(summary = "예약 상세 조회", description = "특정 예약의 상세 정보를 조회합니다")
    @ApiResponse(responseCode = "200", description = "예약 상세 조회 성공")
    @GetMapping("/reservations/{reservationId}")
    public ResponseEntity<CommonResponse<ReservationResponseDto>> getReservation(
            @Parameter(description = "조회할 예약 ID") @PathVariable Long reservationId,
            @Parameter(description = "인증된 사용자 정보") @AuthenticationPrincipal UserDetailsImpl authUser) {

        Long userId = authUser.getId();

        ReservationResponseDto reservation = reservationService.getReservation(reservationId,
                userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_SHOW_RESERVATION, reservation));
    }

    @Operation(summary = "예약 삭제", description = "예약을 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "예약 삭제 성공")
    @DeleteMapping("/reservations/{reservationId}")
    public ResponseEntity<CommonResponse<Void>> deleteReservation(
            @Parameter(description = "삭제할 예약 ID") @PathVariable Long reservationId,
            @Parameter(description = "인증된 사용자 정보") @AuthenticationPrincipal UserDetailsImpl authUser) {

        reservationService.deleteReservation(reservationId, authUser.getId());

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_DELETE_RESERVATION));
    }


}
