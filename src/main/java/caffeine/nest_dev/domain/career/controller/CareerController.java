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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Career", description = "경력 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CareerController {

    private final CareerService careerService;

    // 경력 생성
    @Operation(summary = "경력 생성", description = "프로필에 새로운 경력을 추가합니다")
    @ApiResponse(responseCode = "201", description = "경력 생성 성공")
    @PostMapping("/profiles/{profileId}/careers")
    public ResponseEntity<CommonResponse<CareerResponseDto>> saveCareer(
            @Parameter(description = "경력 생성 요청 정보") @RequestPart("dto") CareerRequestDto dto,
            @Parameter(description = "첨부 파일 목록") @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @Parameter(description = "프로필 ID") @PathVariable Long profileId
    ) {

        CareerResponseDto responseDto = careerService.save(dto, profileId, files);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.of(SuccessCode.SUCCESS_CAREER_CREATED, responseDto));
    }

    // 경력 상세페이지 조회
    @Operation(summary = "경력 상세 조회", description = "특정 경력의 상세 정보를 조회합니다")
    @ApiResponse(responseCode = "200", description = "경력 상세 조회 성공")
    @GetMapping("/profiles/{profileId}/careers/{careerId}")
    public ResponseEntity<CommonResponse<FindCareerResponseDto>> findCareer(
            @Parameter(description = "프로필 ID") @PathVariable Long profileId,
            @Parameter(description = "경력 ID") @PathVariable Long careerId
    ) {

        FindCareerResponseDto responseDto = careerService.findCareer(profileId, careerId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_CAREER_READ, responseDto));
    }

    // 경력 목록 조회
    @Operation(summary = "프로필별 경력 목록 조회", description = "특정 프로필의 경력 목록을 페이징하여 조회합니다")
    @ApiResponse(responseCode = "200", description = "경력 목록 조회 성공")
    @GetMapping("/profiles/{profileId}/careers")
    public ResponseEntity<CommonResponse<PagingResponse<CareersResponseDto>>> findCareers(
            @Parameter(description = "프로필 ID") @PathVariable Long profileId,
            @Parameter(description = "페이지 정보") @PageableDefault Pageable pageable
    ) {

        PagingResponse<CareersResponseDto> careers = careerService.findCareers(profileId, pageable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_CAREERS_READ, careers));
    }

    // 경력 수정
    @Operation(summary = "경력 수정", description = "기존 경력 정보를 수정합니다")
    @ApiResponse(responseCode = "200", description = "경력 수정 성공")
    @PatchMapping("/careers/{careerId}")
    public ResponseEntity<CommonResponse<Void>> updateCareer(
            @Parameter(description = "수정할 경력 ID") @PathVariable Long careerId,
            @Parameter(description = "경력 수정 요청 정보") @RequestBody UpdateCareerRequestDto dto
    ) {

        careerService.updateCareer(careerId, dto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_CAREER_UPDATED));
    }

    // 경력 삭제
    @Operation(summary = "경력 삭제", description = "경력 정보를 삭제합니다")
    @ApiResponse(responseCode = "200", description = "경력 삭제 성공")
    @DeleteMapping("/careers/{careerId}")
    public ResponseEntity<CommonResponse<Void>> deletedCareer(
            @Parameter(description = "삭제할 경력 ID") @PathVariable Long careerId
    ) {

        careerService.deleteCareer(careerId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_CAREER_DELETED));
    }

    // 경력 전체 조회
    @Operation(summary = "전체 경력 조회", description = "인증된 사용자의 모든 경력을 조회합니다")
    @ApiResponse(responseCode = "200", description = "전체 경력 조회 성공")
    @GetMapping("/careers")
    public ResponseEntity<CommonResponse<PagingResponse<CareersResponseDto>>> findAllCareers(
            @Parameter(description = "인증된 사용자 정보") @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Parameter(description = "페이지 정보") @PageableDefault Pageable pageable
    ) {
        PagingResponse<CareersResponseDto> response = careerService.getCareers(pageable, userDetails);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_CAREERS_READ, response));
    }
}
