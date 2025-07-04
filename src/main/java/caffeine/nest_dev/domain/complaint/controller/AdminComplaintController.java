package caffeine.nest_dev.domain.complaint.controller;

import caffeine.nest_dev.common.dto.CommonResponse;
import caffeine.nest_dev.common.dto.PagingResponse;
import caffeine.nest_dev.common.enums.SuccessCode;
import caffeine.nest_dev.domain.complaint.dto.request.AnswerRequestDto;
import caffeine.nest_dev.domain.complaint.dto.request.AnswerUpdateRequestDto;
import caffeine.nest_dev.domain.complaint.dto.response.AnswerResponseDto;
import caffeine.nest_dev.domain.complaint.dto.response.ComplaintResponseDto;
import caffeine.nest_dev.domain.complaint.service.AdminComplaintService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Admin Complaint", description = "관리자 문의/민원 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminComplaintController {

    private final AdminComplaintService adminComplaintService;

    @Operation(summary = "문의 답변 생성", description = "관리자가 문의에 대한 답변을 작성합니다")
    @ApiResponse(responseCode = "201", description = "문의 답변 생성 성공")
    @PostMapping("/complaints/{complaintId}/answer")
    public ResponseEntity<CommonResponse<AnswerResponseDto>> save(
            @Parameter(description = "답변할 문의 ID") @PathVariable Long complaintId,
            @Parameter(description = "인증된 관리자 정보") @AuthenticationPrincipal UserDetailsImpl authUser,
            @Parameter(description = "답변 생성 요청 정보") @RequestBody AnswerRequestDto answerRequestDto
    ) {

        Long userId = authUser.getId();

        AnswerResponseDto answerResponseDto = adminComplaintService.save(userId, complaintId,
                answerRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.of(SuccessCode.SUCCESS_CREATE_ANSWER, answerResponseDto));
    }

    @Operation(summary = "관리자 문의 목록 조회", description = "관리자가 모든 문의 목록을 조회합니다")
    @ApiResponse(responseCode = "200", description = "관리자 문의 목록 조회 성공")
    @GetMapping("/complaints")
    public ResponseEntity<CommonResponse<PagingResponse<ComplaintResponseDto>>> getAllComplaints(
            @Parameter(description = "페이지 정보") @PageableDefault Pageable pageable) {

        PagingResponse<ComplaintResponseDto> getAllComplaintsList = adminComplaintService.getAllComplaints(pageable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_SHOW_COMPLAINTS, getAllComplaintsList));
    }

    @Operation(summary = "관리자 문의 상세 조회", description = "관리자가 특정 문의의 상세 정보를 조회합니다")
    @ApiResponse(responseCode = "200", description = "관리자 문의 상세 조회 성공")
    @GetMapping("/complaints/{complaintId}")
    public ResponseEntity<CommonResponse<ComplaintResponseDto>> getComplaint(
            @Parameter(description = "조회할 문의 ID") @PathVariable Long complaintId) {

        ComplaintResponseDto complaint = adminComplaintService.getComplaint(complaintId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_SHOW_COMPLAINT, complaint));

    }

    @Operation(summary = "관리자 답변 조회", description = "관리자가 특정 문의의 답변을 조회합니다")
    @ApiResponse(responseCode = "200", description = "관리자 답변 조회 성공")
    @GetMapping("/complaints/{complaintId}/answer")
    public ResponseEntity<CommonResponse<AnswerResponseDto>> getAnswer(
            @Parameter(description = "답변을 조회할 문의 ID") @PathVariable Long complaintId){
        AnswerResponseDto answer = adminComplaintService.getAnswer(complaintId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_SHOW_ANSWER, answer));
    }

    @Operation(summary = "답변 수정", description = "관리자가 기존 답변을 수정합니다")
    @ApiResponse(responseCode = "200", description = "답변 수정 성공")
    @PatchMapping("/answers/{answerId}")
    public ResponseEntity<CommonResponse<Void>> update(
            @Parameter(description = "수정할 답변 ID") @PathVariable Long answerId,
            @Parameter(description = "인증된 관리자 정보") @AuthenticationPrincipal UserDetailsImpl authUser,
            @Parameter(description = "답변 수정 요청 정보") @RequestBody AnswerUpdateRequestDto answerUpdateRequestDto){

        Long userId = authUser.getId();

        adminComplaintService.update(userId, answerId, answerUpdateRequestDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_UPDATE_ANSWER));
    }

}
