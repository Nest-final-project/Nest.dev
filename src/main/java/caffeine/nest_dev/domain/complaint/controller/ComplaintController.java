package caffeine.nest_dev.domain.complaint.controller;

import caffeine.nest_dev.common.dto.CommonResponse;
import caffeine.nest_dev.common.dto.PagingResponse;
import caffeine.nest_dev.common.enums.SuccessCode;
import caffeine.nest_dev.domain.complaint.dto.request.ComplaintRequestDto;
import caffeine.nest_dev.domain.complaint.dto.response.ComplaintResponseDto;
import caffeine.nest_dev.domain.complaint.entity.Complaint;
import caffeine.nest_dev.domain.complaint.service.ComplaintService;
import caffeine.nest_dev.domain.review.service.ReviewService;
import caffeine.nest_dev.domain.user.entity.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

        return ResponseEntity.ok(
                CommonResponse.of(SuccessCode.SUCCESS_CREATE_COMPLAINT, complaintResponseDto));
    }

    /**
     * 민원 목록 조회
     */
    @GetMapping("/complaints")
    public ResponseEntity<CommonResponse<PagingResponse<ComplaintResponseDto>>> getComplaints(
            @PageableDefault(page = 0, size = 10)
            Pageable pageable) {
        Page<ComplaintResponseDto> getComplaintList = complaintService.getComplaints(pageable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_SHOW_COMPLAINTS,
                        PagingResponse.from(getComplaintList)));
    }

    @GetMapping("/complaints/{complaintId}")
    public ResponseEntity<CommonResponse<ComplaintResponseDto>> getComplaint(
            @PathVariable Long complaintId) {

        ComplaintResponseDto complaint = complaintService.getComplaint(complaintId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_SHOW_COMPLAINT, complaint));

    }

}
