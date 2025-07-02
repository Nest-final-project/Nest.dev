package caffeine.nest_dev.domain.user.controller;

import caffeine.nest_dev.common.dto.CommonResponse;
import caffeine.nest_dev.common.enums.SuccessCode;
import caffeine.nest_dev.domain.auth.dto.request.DeleteRequestDto;
import caffeine.nest_dev.domain.auth.dto.response.LoginResponseDto;
import caffeine.nest_dev.domain.profile.dto.response.ProfileImageResponseDto;
import caffeine.nest_dev.domain.user.dto.request.ExtraInfoRequestDto;
import caffeine.nest_dev.domain.user.dto.request.UpdatePasswordRequestDto;
import caffeine.nest_dev.domain.user.dto.request.UserRequestDto;
import caffeine.nest_dev.domain.user.dto.response.ProfileImageUploadResponseDto;
import caffeine.nest_dev.domain.user.dto.response.UserInfoResponseDto;
import caffeine.nest_dev.domain.user.dto.response.UserResponseDto;
import caffeine.nest_dev.domain.user.entity.UserDetailsImpl;
import caffeine.nest_dev.domain.user.service.UserService;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // 마이페이지 조회
    @GetMapping("/me")
    public ResponseEntity<CommonResponse<UserResponseDto>> getUser(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {

        UserResponseDto dto = userService.findUser(userDetails.getId());

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_FIND_USER, dto));
    }

    // 유저 조회
    @GetMapping("/{userId}")
    public ResponseEntity<CommonResponse<UserInfoResponseDto>> getUserById(
            @PathVariable Long userId
    ) {
        UserInfoResponseDto responseDto = userService.getUserById(userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_FIND_USER, responseDto));
    }

    // 정보 수정
    @PatchMapping("/me")
    public ResponseEntity<CommonResponse<Void>> updateUser(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody UserRequestDto dto
    ) {

        userService.updateUser(userDetails.getId(), dto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_UPDATE_USER));
    }

    // 비밀번호 수정
    @PatchMapping("/me/password")
    public ResponseEntity<CommonResponse<Void>> updatePassword(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody UpdatePasswordRequestDto dto
    ) {

        userService.updatePassword(userDetails.getId(), dto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_UPDATE_PASSWORD));
    }

    // MENTEE or MENTOR 선택 / 전화번호 입력 (추가정보)
    @PatchMapping("/me/extraInfo")
    public ResponseEntity<CommonResponse<LoginResponseDto>> extraInfo(
            @Valid @RequestBody ExtraInfoRequestDto dto
    ) {

        LoginResponseDto responseDto = userService.updateExtraInfo(dto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_UPDATE_EXTRA_INFO, responseDto));
    }

    // 회원 탈퇴
    @DeleteMapping("/me")
    public ResponseEntity<CommonResponse<Void>> deleteUser(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestHeader("Authorization") String accessToken,
            @RequestBody DeleteRequestDto dto
    ) {

        userService.deleteUser(userDetails.getId(), accessToken, dto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_DELETE_USER));
    }

    // 이미지 저장
    @PostMapping("/profile-image")
    public ResponseEntity<CommonResponse<ProfileImageUploadResponseDto>> saveImage(
            @RequestPart(value = "files", required = false) MultipartFile files,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long userId = userDetails.getId();
        ProfileImageUploadResponseDto responseDto = userService.saveImg(userId, files);
        return ResponseEntity.created(URI.create("/api/users/profile-image")).body(
                CommonResponse.of(SuccessCode.SUCCESS_IMAGE_UPLOAD, responseDto)
        );
    }

    // 이미지 수정
    @PatchMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommonResponse<ProfileImageUploadResponseDto>> updateImage(
            @RequestPart(value = "files", required = false) MultipartFile files,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long userId = userDetails.getId();
        ProfileImageUploadResponseDto responseDto = userService.updateImage(userId, files);
        return ResponseEntity.status(HttpStatus.OK).body(
                CommonResponse.of(SuccessCode.SUCCESS_IMAGE_UPLOAD, responseDto)
        );
    }

    // 프로필 이미지 조회 (멘토/멘티)
    @GetMapping("/{userId}/profile-image")
    public ResponseEntity<CommonResponse<ProfileImageResponseDto>> getUserProfileImage(
            @PathVariable Long userId
    ) {
        ProfileImageResponseDto dto = userService.getUserProfileImage(userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_PROFILE_IMAGE_READ, dto));
    }


    // 이미지 삭제
    @DeleteMapping("/profile-image")
    public ResponseEntity<CommonResponse<Void>> deleteImage(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long userId = userDetails.getId();
        userService.deleteImage(userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_IMAGE_DELETED));
    }
}
