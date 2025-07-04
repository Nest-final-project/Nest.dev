package caffeine.nest_dev.domain.profile.controller;

import caffeine.nest_dev.common.dto.CommonResponse;
import caffeine.nest_dev.common.dto.PagingResponse;
import caffeine.nest_dev.common.enums.SuccessCode;
import caffeine.nest_dev.domain.profile.dto.request.ProfileRequestDto;
import caffeine.nest_dev.domain.profile.dto.response.ProfileResponseDto;
import caffeine.nest_dev.domain.profile.dto.response.RecommendedProfileResponseDto;
import caffeine.nest_dev.domain.profile.service.ProfileService;
import caffeine.nest_dev.domain.user.entity.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Profile", description = "프로필 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProfileController {

    private final ProfileService profileService;

    /**
     * 프로필 생성
     */
    @Operation(summary = "프로필 생성", description = "새로운 프로필을 생성합니다")
    @ApiResponse(responseCode = "201", description = "프로필 생성 성공")
    @PostMapping("/profiles")
    public ResponseEntity<CommonResponse<ProfileResponseDto>> createProfile(
            @Parameter(description = "인증된 사용자 정보") @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Parameter(description = "프로필 생성 요청 정보") @RequestBody ProfileRequestDto profileRequestDto) {
        ProfileResponseDto profile = profileService.createProfile(userDetails.getId(),
                profileRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.of(SuccessCode.SUCCESS_PROFILE_CREATED, profile));
    }

    /**
     * 내 프로필 전체조회
     */
    @Operation(summary = "내 프로필 조회", description = "인증된 사용자의 모든 프로필을 조회합니다")
    @ApiResponse(responseCode = "200", description = "내 프로필 조회 성공")
    @GetMapping("/profiles/me")
    public ResponseEntity<CommonResponse<PagingResponse<ProfileResponseDto>>> getMyProfiles(
            @Parameter(description = "인증된 사용자 정보") @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Parameter(description = "페이지 정보") Pageable pageable
    ) {
        PagingResponse<ProfileResponseDto> myProfiles = profileService.getMyProfiles(
                userDetails.getId(), pageable);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_PROFILE_READ, myProfiles));
    }

    /**
     * 프로필 단건 조회
     */
    @Operation(summary = "프로필 상세 조회", description = "특정 사용자의 특정 프로필을 조회합니다")
    @ApiResponse(responseCode = "200", description = "프로필 상세 조회 성공")
    @GetMapping("/users/{userId}/profiles/{profileId}")
    public ResponseEntity<CommonResponse<ProfileResponseDto>> getProfile(
            @Parameter(description = "사용자 ID") @PathVariable Long userId,
            @Parameter(description = "프로필 ID") @PathVariable Long profileId
    ) {
        ProfileResponseDto profile = profileService.getProfile(userId, profileId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_PROFILE_READ, profile));
    }

    /**
     * 프로필 키워드 검색
     */
    @Operation(summary = "멘토 프로필 키워드 검색", description = "키워드로 멘토 프로필을 검색합니다")
    @ApiResponse(responseCode = "200", description = "멘토 프로필 키워드 검색 성공")
    @GetMapping("/mentors/profiles")
    public ResponseEntity<CommonResponse<List<ProfileResponseDto>>> getMentorProfiles(
            @Parameter(description = "검색 키워드") @RequestParam(required = false) String keyword
    ) {
        List<ProfileResponseDto> profiles = profileService.searchMentorProfilesByKeyword(keyword);

        return ResponseEntity.ok(
                CommonResponse.of(SuccessCode.SUCCESS_PROFILE_KEYWORD_READ, profiles)
        );
    }

    /**
     * 추천 멘토 조회
     */
    @Operation(summary = "추천 멘토 조회", description = "카테고리별 추천 멘토를 조회합니다")
    @ApiResponse(responseCode = "200", description = "추천 멘토 조회 성공")
    @GetMapping("/mentors/recommended-profiles")
    public ResponseEntity<CommonResponse<List<RecommendedProfileResponseDto>>> getRecommendedProfiles(
            @Parameter(description = "카테고리 ID") @RequestParam Long categoryId
    ) {
        List<RecommendedProfileResponseDto> responseDto = profileService.getRecommendedProfiles(
                categoryId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_RECOMMENDED_PROFILE_READ, responseDto));
    }

    /**
     * 프로필 수정
     */
    @Operation(summary = "프로필 수정", description = "기존 프로필 정보를 수정합니다")
    @ApiResponse(responseCode = "200", description = "프로필 수정 성공")
    @PatchMapping("/profiles/{profileId}")
    public ResponseEntity<CommonResponse<Void>> updateProfile(
            @Parameter(description = "인증된 사용자 정보") @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Parameter(description = "수정할 프로필 ID") @PathVariable Long profileId,
            @Parameter(description = "프로필 수정 요청 정보") @RequestBody ProfileRequestDto profileRequestDto) {

        profileService.updateProfile(userDetails.getId(), profileId, profileRequestDto);

        return ResponseEntity.ok(
                CommonResponse.of(SuccessCode.SUCCESS_PROFILE_UPDATED)
        );
    }

    @Operation(summary = "프로필 삭제", description = "프로필을 삭제합니다")
    @ApiResponse(responseCode = "200", description = "프로필 삭제 성공")
    @DeleteMapping("/profiles/{profileId}")
    public ResponseEntity<CommonResponse<Void>> deleteProfile(
            @Parameter(description = "인증된 사용자 정보") @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Parameter(description = "삭제할 프로필 ID") @PathVariable Long profileId
    ) {
        profileService.deleteProfile(userDetails.getId(), profileId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_PROFILE_DELETED));
    }
}
