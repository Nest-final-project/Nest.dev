package caffeine.nest_dev.domain.complaint.controller;

import caffeine.nest_dev.common.dto.CommonResponse;
import caffeine.nest_dev.common.enums.SuccessCode;
import caffeine.nest_dev.domain.complaint.dto.request.ComplaintRequestDto;
import caffeine.nest_dev.domain.complaint.dto.response.ComplaintResponseDto;
import caffeine.nest_dev.domain.complaint.service.ComplaintService;
import caffeine.nest_dev.domain.user.entity.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
                    @AuthenticationPrincipal UserDetailsImpl authUser){

        Long userId = authUser.getId();

        ComplaintResponseDto complaintResponseDto = complaintService.save(userId, complaintRequestDto);

        return ResponseEntity.ok(CommonResponse.of(SuccessCode.SUCCESS_CREATE_COMPLAINT, complaintResponseDto));
    }
}
