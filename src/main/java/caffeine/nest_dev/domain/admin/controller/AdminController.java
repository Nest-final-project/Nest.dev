package caffeine.nest_dev.domain.admin.controller;

import caffeine.nest_dev.common.dto.CommonResponse;
import caffeine.nest_dev.common.dto.PagingResponse;
import caffeine.nest_dev.common.enums.SuccessCode;
import caffeine.nest_dev.domain.admin.dto.request.AdminRequestDto;
import caffeine.nest_dev.domain.admin.dto.response.AdminMentorCareerResponseDto;
import caffeine.nest_dev.domain.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AdminController {

    private final AdminService adminService;

    /**
     * 멘토 경력 확인 요청 목록조회
     */
    @GetMapping("/admin/mentor-careers")
    public ResponseEntity<CommonResponse<PagingResponse<AdminMentorCareerResponseDto>>> getMentorCareers(Pageable pageable) {
        PagingResponse<AdminMentorCareerResponseDto> pagingResponse = adminService.getMentorCareers(pageable);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_ADMIN_MENTOR_CAREER_READ, pagingResponse));
    }

    /**
     * 멘토 경력 확인 요청 단건조회
     */
    @GetMapping("/admin/mentor-careers/{careerId}")
    public ResponseEntity<CommonResponse<AdminMentorCareerResponseDto>> getMentorCareerById(
            @PathVariable Long careerId) {
        AdminMentorCareerResponseDto responseDto = adminService.getMentorCareerById(careerId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_ADMIN_MENTOR_CAREER_DETAIL_READ, responseDto));
    }

    /**
     * 멘토 경력 확인 요청 상태수정
     */
    @PatchMapping("/admin/mentor-careers/{careerId}/status")
    public ResponseEntity<CommonResponse> updateMentorCareerStatus(
            @PathVariable Long careerId,
            @RequestBody AdminRequestDto adminRequestDto
    ){
        adminService.updateCareerStatus(careerId, adminRequestDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_ADMIN_MENTOR_CAREER_STATUS_UPDATED));
    }

}
