package caffeine.nest_dev.domain.career.controller;

import caffeine.nest_dev.common.dto.CommonResponse;
import caffeine.nest_dev.common.dto.PagingResponse;
import caffeine.nest_dev.common.enums.SuccessCode;
import caffeine.nest_dev.domain.career.dto.request.CareerRequestDto;
import caffeine.nest_dev.domain.career.dto.response.CareerResponseDto;
import caffeine.nest_dev.domain.career.dto.response.CareersResponseDto;
import caffeine.nest_dev.domain.career.dto.response.FindCareerResponseDto;
import caffeine.nest_dev.domain.career.service.CareerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profiles/{profileId}/careers")
public class CareerController {

    private final CareerService careerService;

    // 경력 생성
    @PostMapping
    public ResponseEntity<CommonResponse<CareerResponseDto>> saveCareer(
            @RequestBody CareerRequestDto dto,
            @PathVariable Long profileId
    ) {

        CareerResponseDto responseDto = careerService.save(dto, profileId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.of(SuccessCode.SUCCESS_CAREER_CREATED, responseDto));
    }

    // 경력 상세페이지 조회
    @GetMapping("/{careerId}")
    public ResponseEntity<CommonResponse<FindCareerResponseDto>> findCareer(
            @PathVariable Long profileId,
            @PathVariable Long careerId
    ) {

        FindCareerResponseDto responseDto = careerService.findCareer(profileId, careerId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_CAREER_READ, responseDto));
    }

    // 경력 목록 조회
    @GetMapping
    public ResponseEntity<CommonResponse<PagingResponse<CareersResponseDto>>> findCareers(
            @PathVariable Long profileId,
            @PageableDefault Pageable pageable
    ) {

        PagingResponse<CareersResponseDto> careers = careerService.findCareers(profileId, pageable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_CAREERS_READ, careers));
    }
}
