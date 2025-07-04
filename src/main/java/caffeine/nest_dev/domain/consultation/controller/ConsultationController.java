package caffeine.nest_dev.domain.consultation.controller;

import caffeine.nest_dev.common.dto.CommonResponse;
import caffeine.nest_dev.common.enums.SuccessCode;
import caffeine.nest_dev.domain.consultation.dto.request.ConsultationRequestDto;
import caffeine.nest_dev.domain.consultation.dto.response.AvailableSlotDto;
import caffeine.nest_dev.domain.consultation.dto.response.ConsultationResponseDto;
import caffeine.nest_dev.domain.consultation.service.ConsultationService;
import caffeine.nest_dev.domain.user.entity.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Consultation", description = "상담 시간 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ConsultationController {

    private final ConsultationService consultationService;

    /**
     * 상감 가능 시간 등록
     */
    @Operation(summary = "상담 가능 시간 등록", description = "멘토가 상담 가능한 시간을 등록합니다")
    @ApiResponse(responseCode = "201", description = "상담 가능 시간 등록 성공")
    @PostMapping("/mentor/consultations")
    public ResponseEntity<CommonResponse<ConsultationResponseDto>> createConsultation(
            @Parameter(description = "인증된 멘토 정보") @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Parameter(description = "상담 시간 등록 요청 정보") @RequestBody ConsultationRequestDto consultationRequestDto
    ) {
        ConsultationResponseDto consultation = consultationService.createConsultation(
                userDetails.getId(), consultationRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.of(SuccessCode.SUCCESS_CONSULTATION_CREATED, consultation));

    }

    /**
     * 내가 등록한 상담 시간 조회
     */
    @Operation(summary = "내 상담 시간 조회", description = "멘토가 자신이 등록한 상담 시간을 조회합니다")
    @ApiResponse(responseCode = "200", description = "내 상담 시간 조회 성공")
    @GetMapping("/mentor/consultations")
    public ResponseEntity<CommonResponse<List<ConsultationResponseDto>>> getMyConsultations(
            @Parameter(description = "인증된 멘토 정보") @AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<ConsultationResponseDto> myConsultations = consultationService.getMyConsultations(
                userDetails.getId());

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_CONSULTATION_READ, myConsultations));
    }

    /**
     * MENTEE 가 보는 예약된 시간을 제외한 상담 가능한 시간 조회
     */
    @Operation(summary = "상담 가능 시간 조회", description = "멘티가 특정 멘토의 상담 가능한 시간을 조회합니다")
    @ApiResponse(responseCode = "200", description = "상담 가능 시간 조회 성공")
    @GetMapping("/mentor/{mentorId}/availableConsultations")
    public ResponseEntity<CommonResponse<List<AvailableSlotDto>>> getSlots(
            @Parameter(description = "멘토 ID") @PathVariable Long mentorId, // 멘토의 ID
            @Parameter(description = "조회할 날짜") @RequestParam LocalDate localDate
    ) {
        List<AvailableSlotDto> responseDto = consultationService.getAvailableConsultationSlots(
                mentorId, localDate);
        return ResponseEntity.ok(CommonResponse.of(SuccessCode.SUCCESS_SLOTS_READ, responseDto));
    }

    @Operation(summary = "상담 시간 삭제", description = "멘토가 등록한 상담 시간을 삭제합니다")
    @ApiResponse(responseCode = "200", description = "상담 시간 삭제 성공")
    @DeleteMapping("/mentor/consultations/{consultationId}")
    public ResponseEntity<CommonResponse<Void>> deleteConsultation(
            @Parameter(description = "인증된 멘토 정보") @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Parameter(description = "삭제할 상담 시간 ID") @PathVariable Long consultationId) {
        consultationService.deleteConsultation(userDetails.getId(), consultationId);
        return ResponseEntity.ok(CommonResponse.of(SuccessCode.SUCCESS_CONSULTATION_DELETED));
    }
}
