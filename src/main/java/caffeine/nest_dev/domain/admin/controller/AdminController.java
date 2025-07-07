package caffeine.nest_dev.domain.admin.controller;

import caffeine.nest_dev.common.dto.CommonResponse;
import caffeine.nest_dev.common.dto.PagingResponse;
import caffeine.nest_dev.common.enums.SuccessCode;
import caffeine.nest_dev.domain.admin.dto.request.AdminRequestDto;
import caffeine.nest_dev.domain.admin.dto.response.AdminMentorCareerResponseDto;
import caffeine.nest_dev.domain.admin.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Admin", description = "관리자 API - 멘토 경력 관리")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AdminController {

    private final AdminService adminService;

    /**
     * 멘토 경력 확인 요청 목록조회
     */
    @Operation(summary = "멘토 경력 확인 요청 목록 조회", description = "페이징 처리된 멘토 경력 확인 요청 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 조회됨",
            content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    @GetMapping("/admin/mentor-careers")
    public ResponseEntity<CommonResponse<PagingResponse<AdminMentorCareerResponseDto>>> getMentorCareers(
            Pageable pageable) {
        PagingResponse<AdminMentorCareerResponseDto> pagingResponse = adminService.getMentorCareers(
                pageable);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_ADMIN_MENTOR_CAREER_READ,
                        pagingResponse));
    }

    /**
     * 멘토 경력 확인 요청 단건조회
     */
    @Operation(summary = "멘토 경력 확인 요청 단건 조회", description = "특정 ID의 멘토 경력 확인 요청을 상세 조회합니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 조회됨",
            content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    @GetMapping("/admin/mentor-careers/{careerId}")
    public ResponseEntity<CommonResponse<AdminMentorCareerResponseDto>> getMentorCareerById(
            @Parameter(description = "조회할 멘토 경력 ID", required = true)
            @PathVariable Long careerId) {
        AdminMentorCareerResponseDto responseDto = adminService.getMentorCareerById(careerId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_ADMIN_MENTOR_CAREER_DETAIL_READ,
                        responseDto));
    }

    /**
     * * 멘토 경력 확인 요청 상태수정
     **/
    @Operation(summary = "멘토 경력 확인 요청 상태 수정", description = "특정 멘토 경력 확인 요청의 승인/거부 상태를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "상태가 성공적으로 수정됨",
            content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    @PatchMapping("/admin/mentor-careers/{careerId}/status")
    public ResponseEntity<CommonResponse<Void>> updateMentorCareerStatus(
            @Parameter(description = "수정할 멘토 경력 ID", required = true)
            @PathVariable Long careerId,
            @Parameter(description = "상태 수정 요청 데이터", required = true)
            @RequestBody AdminRequestDto adminRequestDto) {
        adminService.updateCareerStatus(careerId, adminRequestDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_ADMIN_MENTOR_CAREER_STATUS_UPDATED));
    }

}
