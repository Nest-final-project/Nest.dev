package caffeine.nest_dev.domain.career.controller;

import caffeine.nest_dev.common.dto.CommonResponse;
import caffeine.nest_dev.common.dto.PagingResponse;
import caffeine.nest_dev.common.enums.SuccessCode;
import caffeine.nest_dev.domain.career.dto.request.CareerRequestDto;
import caffeine.nest_dev.domain.career.dto.request.UpdateCareerRequestDto;
import caffeine.nest_dev.domain.career.dto.response.CareerResponseDto;
import caffeine.nest_dev.domain.career.dto.response.CareersResponseDto;
import caffeine.nest_dev.domain.career.dto.response.FindCareerResponseDto;
import caffeine.nest_dev.domain.career.service.CareerService;
import caffeine.nest_dev.domain.user.entity.UserDetailsImpl;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CareerController {

    private final CareerService careerService;

    // 경력 생성
    @PostMapping("/profiles/{profileId}/careers")
    public ResponseEntity<CommonResponse<CareerResponseDto>> saveCareer(
            @RequestPart("dto") CareerRequestDto dto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @PathVariable Long profileId
    ) {

        CareerResponseDto responseDto = careerService.save(dto, profileId, files);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.of(SuccessCode.SUCCESS_CAREER_CREATED, responseDto));
    }

    // 경력 상세페이지 조회
    @GetMapping("/profiles/{profileId}/careers/{careerId}")
    public ResponseEntity<CommonResponse<FindCareerResponseDto>> findCareer(
            @PathVariable Long profileId,
            @PathVariable Long careerId
    ) {

        FindCareerResponseDto responseDto = careerService.findCareer(profileId, careerId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_CAREER_READ, responseDto));
    }

    // 경력 목록 조회
    @GetMapping("/profiles/{profileId}/careers")
    public ResponseEntity<CommonResponse<PagingResponse<CareersResponseDto>>> findCareers(
            @PathVariable Long profileId,
            @PageableDefault Pageable pageable
    ) {

        PagingResponse<CareersResponseDto> careers = careerService.findCareers(profileId, pageable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_CAREERS_READ, careers));
    }

    // 경력 수정
    @PatchMapping("/careers/{careerId}")
    public ResponseEntity<CommonResponse<Void>> updateCareer(
            @PathVariable Long careerId,
            @RequestBody UpdateCareerRequestDto dto
    ) {

        careerService.updateCareer(careerId, dto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_CAREER_UPDATED));
    }

    // 경력 삭제
    @DeleteMapping("/careers/{careerId}")
    public ResponseEntity<CommonResponse<Void>> deletedCareer(
            @PathVariable Long careerId
    ) {

        careerService.deleteCareer(careerId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_CAREER_DELETED));
    }

    // 경력 전체 조회
    @GetMapping("/careers")
    public ResponseEntity<CommonResponse<PagingResponse<CareersResponseDto>>> findAllCareers(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PageableDefault Pageable pageable
    ) {
        PagingResponse<CareersResponseDto> response = careerService.getCareers(pageable, userDetails);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_CAREERS_READ, response));
    }
}
