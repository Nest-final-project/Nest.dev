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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminComplaintController {

    private final AdminComplaintService adminComplaintService;


    @PostMapping("/complaints/{complaintId}/answer")
    public ResponseEntity<CommonResponse<AnswerResponseDto>> save(@PathVariable Long complaintId,
            @AuthenticationPrincipal UserDetailsImpl authUser,
            @RequestBody AnswerRequestDto answerRequestDto
    ) {

        Long userId = authUser.getId();

        AnswerResponseDto answerResponseDto = adminComplaintService.save(userId, complaintId,
                answerRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.of(SuccessCode.SUCCESS_CREATE_ANSWER, answerResponseDto));
    }

    @GetMapping("/complaints")
    public ResponseEntity<CommonResponse<PagingResponse<ComplaintResponseDto>>> getAllComplaints(
            @PageableDefault Pageable pageable) {

        PagingResponse<ComplaintResponseDto> getAllComplaintsList = adminComplaintService.getAllComplaints(pageable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_SHOW_COMPLAINTS, getAllComplaintsList));
    }

    @GetMapping("/complaints/{complaintId}")
    public ResponseEntity<CommonResponse<ComplaintResponseDto>> getComplaint(
            @PathVariable Long complaintId) {

        ComplaintResponseDto complaint = adminComplaintService.getComplaint(complaintId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_SHOW_COMPLAINT, complaint));

    }

    @PatchMapping("/answers/{answerId}")
    public ResponseEntity<CommonResponse<Void>> update(@PathVariable Long answerId,
            @AuthenticationPrincipal UserDetailsImpl authUser,
            @RequestBody AnswerUpdateRequestDto answerUpdateRequestDto){

        Long userId = authUser.getId();

        adminComplaintService.update(userId, answerId, answerUpdateRequestDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_UPDATE_ANSWER));
    }

}
