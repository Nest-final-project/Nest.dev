package caffeine.nest_dev.domain.profile.controller;

import caffeine.nest_dev.common.dto.CommonResponse;
import caffeine.nest_dev.domain.profile.dto.request.ProfileRequestDto;
import caffeine.nest_dev.domain.profile.dto.response.ProfileResponseDto;
import caffeine.nest_dev.domain.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProfileController {
    private final ProfileService profileService;

//    /**
//     * 프로필 생성
//     * */
//    @PostMapping("/users/{userId}/profiles")
//    public ResponseEntity<CommonResponse<ProfileResponseDto>> createProfile(
//            @PathVariable Long userId,
//            @RequestBody ProfileRequestDto profileRequestDto) {
//        profileService.createProfile(userId, profileRequestDto);
//    }
}
