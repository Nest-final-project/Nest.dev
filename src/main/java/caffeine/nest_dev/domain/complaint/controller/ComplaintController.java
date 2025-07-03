package caffeine.nest_dev.domain.complaint.controller;

import caffeine.nest_dev.common.dto.CommonResponse;
import caffeine.nest_dev.common.dto.PagingResponse;
import caffeine.nest_dev.common.enums.SuccessCode;
import caffeine.nest_dev.domain.complaint.dto.request.ComplaintRequestDto;
import caffeine.nest_dev.domain.complaint.dto.response.AnswerResponseDto;
import caffeine.nest_dev.domain.complaint.dto.response.ComplaintResponseDto;
import caffeine.nest_dev.domain.complaint.service.ComplaintService;
import caffeine.nest_dev.domain.user.entity.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

@Tag(name = "Complaint", description = "문의/민원 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ComplaintController {

    private final ComplaintService complaintService;

    @Operation(summary = "문의 생성", description = "새로운 문의를 등록합니다")
    @ApiResponse(responseCode = "201", description = "문의 생성 성공")
    @PostMapping("/complaints")
    public ResponseEntity<CommonResponse<ComplaintResponseDto>> save
            (@Parameter(description = "문의 생성 요청 정보") @RequestBody ComplaintRequestDto complaintRequestDto,
                    @Parameter(description = "인증된 사용자 정보") @AuthenticationPrincipal UserDetailsImpl authUser) {

        Long userId = authUser.getId();

        ComplaintResponseDto complaintResponseDto = complaintService.save(userId,
                complaintRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.of(SuccessCode.SUCCESS_CREATE_COMPLAINT, complaintResponseDto));
    }

    /**
     * 문의 목록 조회(다른 사용자들도 조회 가능)
     */
    @Operation(summary = "전체 문의 목록 조회", description = "모든 사용자의 문의 목록을 조회합니다")
    @ApiResponse(responseCode = "200", description = "전체 문의 목록 조회 성공")
    @GetMapping("/complaints")
    public ResponseEntity<CommonResponse<PagingResponse<ComplaintResponseDto>>> getInquiries(
            @Parameter(description = "페이지 정보") @PageableDefault(direction = Sort.Direction.DESC)
            Pageable pageable) {
        PagingResponse<ComplaintResponseDto> getComplaintList = complaintService.getInquiries(pageable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_SHOW_COMPLAINTS, getComplaintList));
    }

    /**
     * 문의 상세 조회(다른 사용자들 문의도 조회 가능)
     * @param complaintId
     * @return
     */
    @Operation(summary = "문의 상세 조회", description = "특정 문의의 상세 정보를 조회합니다")
    @ApiResponse(responseCode = "200", description = "문의 상세 조회 성공")
    @GetMapping("/complaints/{complaintId}")
    public ResponseEntity<CommonResponse<ComplaintResponseDto>> getComplaint(
            @Parameter(description = "문의 ID") @PathVariable Long complaintId) {

        ComplaintResponseDto complaint = complaintService.getComplaint(complaintId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_SHOW_COMPLAINT, complaint));

    }
    /**
     * 민원 목록 조회(본인의 민원 목록만)
     */
    @Operation(summary = "내 문의 목록 조회", description = "인증된 사용자의 문의 목록만 조회합니다")
    @ApiResponse(responseCode = "200", description = "내 문의 목록 조회 성공")
    @GetMapping("/complaints/myComplaints")
    public ResponseEntity<CommonResponse<PagingResponse<ComplaintResponseDto>>> getMyComplaints(
            @Parameter(description = "인증된 사용자 정보") @AuthenticationPrincipal UserDetailsImpl authUser,
            @Parameter(description = "페이지 정보") @PageableDefault(direction = Sort.Direction.DESC)
            Pageable pageable
    ){
        Long userId = authUser.getId();
        PagingResponse<ComplaintResponseDto> getMyComplaints = complaintService.getMyComplaints(userId, pageable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_SHOW_COMPLAINTS, getMyComplaints));

    }

    @Operation(summary = "문의 답변 조회", description = "특정 문의에 대한 답변을 조회합니다")
    @ApiResponse(responseCode = "200", description = "문의 답변 조회 성공")
    @GetMapping("/complaints/{complaintId}/answer")
    public ResponseEntity<CommonResponse<AnswerResponseDto>> getAnswer(
            @Parameter(description = "문의 ID") @PathVariable Long complaintId,
            @Parameter(description = "인증된 사용자 정보") @AuthenticationPrincipal UserDetailsImpl authUser){

        AnswerResponseDto answer = complaintService.getAnswer(authUser.getId(), complaintId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_SHOW_ANSWER, answer));
    }


    /**
     * 민원 삭제(본인의 민원)
     */
    @Operation(summary = "문의 삭제", description = "본인의 문의를 삭제합니다")
    @ApiResponse(responseCode = "200", description = "문의 삭제 성공")
    @DeleteMapping("/complaints/{complaintId}")
    public ResponseEntity<CommonResponse<Void>> deleteComplaint(
            @Parameter(description = "삭제할 문의 ID") @PathVariable Long complaintId,
            @Parameter(description = "인증된 사용자 정보") @AuthenticationPrincipal UserDetailsImpl authUser
    ){
        Long id = authUser.getId();
        complaintService.deleteComplaint(id, complaintId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_DELETE_COMPLAINT));
    }
}
