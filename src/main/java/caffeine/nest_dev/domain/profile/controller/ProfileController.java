package caffeine.nest_dev.domain.profile.controller;

import caffeine.nest_dev.common.dto.CommonResponse;
import caffeine.nest_dev.common.dto.PagingResponse;
import caffeine.nest_dev.common.enums.SuccessCode;
import caffeine.nest_dev.domain.profile.dto.request.ProfileRequestDto;
import caffeine.nest_dev.domain.profile.dto.response.ProfileResponseDto;
import caffeine.nest_dev.domain.profile.dto.response.RecommendedProfileResponseDto;
import caffeine.nest_dev.domain.profile.service.ProfileService;
import caffeine.nest_dev.domain.user.entity.UserDetailsImpl;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProfileController {

    private final ProfileService profileService;

    /**
     * 프로필 생성
     */
    @PostMapping("/profiles")
    public ResponseEntity<CommonResponse<ProfileResponseDto>> createProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody ProfileRequestDto profileRequestDto) {
        ProfileResponseDto profile = profileService.createProfile(userDetails.getId(),
                profileRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.of(SuccessCode.SUCCESS_PROFILE_CREATED, profile));
    }

    /**
     * 내 프로필 전체조회
     */
    @GetMapping("/profiles/me")
    public ResponseEntity<CommonResponse<PagingResponse<ProfileResponseDto>>> getMyProfiles(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            Pageable pageable
    ) {
        PagingResponse<ProfileResponseDto> myProfiles = profileService.getMyProfiles(
                userDetails.getId(), pageable);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_PROFILE_READ, myProfiles));
    }

    /**
     * 프로필 단건 조회
     */
    @GetMapping("/users/{userId}/profiles/{profileId}")
    public ResponseEntity<CommonResponse<ProfileResponseDto>> getProfile(
            @PathVariable Long userId,
            @PathVariable Long profileId
    ) {
        ProfileResponseDto profile = profileService.getProfile(userId, profileId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_PROFILE_READ, profile));
    }

    /**
     * 프로필 키워드 검색
     */
    @GetMapping("/mentors/profiles")
    public ResponseEntity<CommonResponse<List<ProfileResponseDto>>> getMentorProfiles(
            @RequestParam(required = false) String keyword
    ) {
        List<ProfileResponseDto> profiles = profileService.searchMentorProfilesByKeyword(keyword);

        return ResponseEntity.ok(
                CommonResponse.of(SuccessCode.SUCCESS_PROFILE_KEYWORD_READ, profiles)
        );
    }

    /**
     * 추천 멘토 조회
     */
    @GetMapping("/mentors/recommended-profiles")
    public ResponseEntity<CommonResponse<List<RecommendedProfileResponseDto>>> getRecommendedProfiles(
            @RequestParam Long categoryId
    ) {
        List<RecommendedProfileResponseDto> responseDto = profileService.getRecommendedProfiles(
                categoryId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_RECOMMENDED_PROFILE_READ, responseDto));
    }

    /**
     * 프로필 수정
     */
    @PatchMapping("/profiles/{profileId}")
    public ResponseEntity<CommonResponse<Void>> updateProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long profileId,
            @RequestBody ProfileRequestDto profileRequestDto) {

        profileService.updateProfile(userDetails.getId(), profileId, profileRequestDto);

        return ResponseEntity.ok(
                CommonResponse.of(SuccessCode.SUCCESS_PROFILE_UPDATED)
        );
    }

}
