package caffeine.nest_dev.domain.complaint.controller;

import caffeine.nest_dev.common.dto.CommonResponse;
import caffeine.nest_dev.common.dto.PagingResponse;
import caffeine.nest_dev.common.enums.SuccessCode;
import caffeine.nest_dev.domain.complaint.dto.request.ComplaintRequestDto;
import caffeine.nest_dev.domain.complaint.dto.response.AnswerResponseDto;
import caffeine.nest_dev.domain.complaint.dto.response.ComplaintResponseDto;
import caffeine.nest_dev.domain.complaint.service.ComplaintService;
import caffeine.nest_dev.domain.user.entity.UserDetailsImpl;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ComplaintController {

    private final ComplaintService complaintService;


    @PostMapping("/complaints")
    public ResponseEntity<CommonResponse<ComplaintResponseDto>> save
            (@RequestBody ComplaintRequestDto complaintRequestDto,
                    @AuthenticationPrincipal UserDetailsImpl authUser) {

        Long userId = authUser.getId();

        ComplaintResponseDto complaintResponseDto = complaintService.save(userId,
                complaintRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.of(SuccessCode.SUCCESS_CREATE_COMPLAINT, complaintResponseDto));
    }

    /**
     * 문의 목록 조회(다른 사용자들도 조회 가능)
     */
    @GetMapping("/complaints")
    public ResponseEntity<CommonResponse<PagingResponse<ComplaintResponseDto>>> getInquiries(
            @PageableDefault(direction = Sort.Direction.DESC)
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
    @GetMapping("/complaints/{complaintId}")
    public ResponseEntity<CommonResponse<ComplaintResponseDto>> getComplaint(
            @PathVariable Long complaintId) {

        ComplaintResponseDto complaint = complaintService.getComplaint(complaintId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_SHOW_COMPLAINT, complaint));

    }
    /**
     * 민원 목록 조회(본인의 민원 목록만)
     */
    @GetMapping("/complaints/myComplaints")
    public ResponseEntity<CommonResponse<PagingResponse<ComplaintResponseDto>>> getMyComplaints(
            @AuthenticationPrincipal UserDetailsImpl authUser,
            @PageableDefault(direction = Sort.Direction.DESC)
            Pageable pageable
    ){
        Long userId = authUser.getId();
        PagingResponse<ComplaintResponseDto> getMyComplaints = complaintService.getMyComplaints(userId, pageable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_SHOW_COMPLAINTS, getMyComplaints));

    }

    @GetMapping("/complaints/{complaintId}/answers")
    public ResponseEntity<CommonResponse<AnswerResponseDto>> getAnswer(@PathVariable Long complaintId,
            @AuthenticationPrincipal UserDetailsImpl authUser){

        AnswerResponseDto answer = complaintService.getAnswer(authUser.getId(), complaintId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_SHOW_ANSWER, answer));
    }


    /**
     * 민원 삭제(본인의 민원)
     */
    @DeleteMapping("/complaints/{complaintId}")
    public ResponseEntity<CommonResponse<Void>> deleteComplaint(
            @PathVariable Long complaintId,
            @AuthenticationPrincipal UserDetailsImpl authUser
    ){
        Long id = authUser.getId();
        complaintService.deleteComplaint(id, complaintId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_DELETE_COMPLAINT));
    }
}
