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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "User", description = "사용자 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // 마이페이지 조회
    @Operation(summary = "내 정보 조회", description = "인증된 사용자의 정보를 조회합니다")
    @ApiResponse(responseCode = "200", description = "내 정보 조회 성공")
    @GetMapping("/me")
    public ResponseEntity<CommonResponse<UserResponseDto>> getUser(
            @Parameter(description = "인증된 사용자 정보") @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {

        UserResponseDto dto = userService.findUser(userDetails.getId());

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_FIND_USER, dto));
    }

    // 유저 조회
    @Operation(summary = "사용자 정보 조회", description = "특정 사용자의 정보를 조회합니다")
    @ApiResponse(responseCode = "200", description = "사용자 정보 조회 성공")
    @GetMapping("/{userId}")
    public ResponseEntity<CommonResponse<UserInfoResponseDto>> getUserById(
            @Parameter(description = "조회할 사용자 ID") @PathVariable Long userId
    ) {
        UserInfoResponseDto responseDto = userService.getUserById(userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_FIND_USER, responseDto));
    }

    // 정보 수정
    @Operation(summary = "내 정보 수정", description = "인증된 사용자의 정보를 수정합니다")
    @ApiResponse(responseCode = "200", description = "내 정보 수정 성공")
    @PatchMapping("/me")
    public ResponseEntity<CommonResponse<Void>> updateUser(
            @Parameter(description = "인증된 사용자 정보") @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Parameter(description = "사용자 정보 수정 요청") @Valid @RequestBody UserRequestDto dto
    ) {

        userService.updateUser(userDetails.getId(), dto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_UPDATE_USER));
    }

    // 비밀번호 수정
    @Operation(summary = "비밀번호 수정", description = "사용자의 비밀번호를 수정합니다")
    @ApiResponse(responseCode = "200", description = "비밀번호 수정 성공")
    @PatchMapping("/me/password")
    public ResponseEntity<CommonResponse<Void>> updatePassword(
            @Parameter(description = "인증된 사용자 정보") @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Parameter(description = "비밀번호 수정 요청 정보") @Valid @RequestBody UpdatePasswordRequestDto dto
    ) {

        userService.updatePassword(userDetails.getId(), dto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_UPDATE_PASSWORD));
    }

    // MENTEE or MENTOR 선택 / 전화번호 입력 (추가정보)
    @Operation(summary = "추가 정보 입력", description = "사용자 유형(멘토/멘티) 및 전화번호 등 추가 정보를 입력합니다")
    @ApiResponse(responseCode = "200", description = "추가 정보 입력 성공")
    @PatchMapping("/me/extraInfo")
    public ResponseEntity<CommonResponse<LoginResponseDto>> extraInfo(
            @Parameter(description = "추가 정보 입력 요청") @Valid @RequestBody ExtraInfoRequestDto dto
    ) {

        LoginResponseDto responseDto = userService.updateExtraInfo(dto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_UPDATE_EXTRA_INFO, responseDto));
    }

    // 회원 탈퇴
    @Operation(summary = "회원 탈퇴", description = "사용자 계정을 삭제합니다")
    @ApiResponse(responseCode = "200", description = "회원 탈퇴 성공")
    @DeleteMapping("/me")
    public ResponseEntity<CommonResponse<Void>> deleteUser(
            @Parameter(description = "인증된 사용자 정보") @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Parameter(description = "JWT 액세스 토큰") @RequestHeader("Authorization") String accessToken,
            @Parameter(description = "회원 탈퇴 요청 정보") @RequestBody DeleteRequestDto dto
    ) {

        userService.deleteUser(userDetails.getId(), accessToken, dto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_DELETE_USER));
    }

    // 이미지 저장
    @Operation(summary = "프로필 이미지 업로드", description = "사용자의 프로필 이미지를 업로드합니다")
    @ApiResponse(responseCode = "201", description = "프로필 이미지 업로드 성공")
    @PostMapping("/profile-image")
    public ResponseEntity<CommonResponse<ProfileImageUploadResponseDto>> saveImage(
            @Parameter(description = "업로드할 이미지 파일") @RequestPart(value = "files", required = false) MultipartFile files,
            @Parameter(description = "인증된 사용자 정보") @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long userId = userDetails.getId();
        ProfileImageUploadResponseDto responseDto = userService.saveImg(userId, files);
        return ResponseEntity.created(URI.create("/api/users/profile-image")).body(
                CommonResponse.of(SuccessCode.SUCCESS_IMAGE_UPLOAD, responseDto)
        );
    }

    // 이미지 수정
    @Operation(summary = "프로필 이미지 수정", description = "사용자의 프로필 이미지를 수정합니다")
    @ApiResponse(responseCode = "200", description = "프로필 이미지 수정 성공")
    @PatchMapping(value = "/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommonResponse<ProfileImageUploadResponseDto>> updateImage(
            @Parameter(description = "수정할 이미지 파일") @RequestPart(value = "files", required = false) MultipartFile files,
            @Parameter(description = "인증된 사용자 정보") @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long userId = userDetails.getId();
        ProfileImageUploadResponseDto responseDto = userService.updateImage(userId, files);
        return ResponseEntity.status(HttpStatus.OK).body(
                CommonResponse.of(SuccessCode.SUCCESS_IMAGE_UPLOAD, responseDto)
        );
    }

    // 프로필 이미지 조회 (멘토/멘티)
    @Operation(summary = "사용자 프로필 이미지 조회", description = "특정 사용자의 프로필 이미지를 조회합니다")
    @ApiResponse(responseCode = "200", description = "사용자 프로필 이미지 조회 성공")
    @GetMapping("/{userId}/profile-image")
    public ResponseEntity<CommonResponse<ProfileImageResponseDto>> getUserProfileImage(
            @Parameter(description = "조회할 사용자 ID") @PathVariable Long userId
    ) {
        ProfileImageResponseDto dto = userService.getUserProfileImage(userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_PROFILE_IMAGE_READ, dto));
    }


    // 이미지 삭제
    @Operation(summary = "프로필 이미지 삭제", description = "사용자의 프로필 이미지를 삭제합니다")
    @ApiResponse(responseCode = "200", description = "프로필 이미지 삭제 성공")
    @DeleteMapping("/profile-image")
    public ResponseEntity<CommonResponse<Void>> deleteImage(
            @Parameter(description = "인증된 사용자 정보") @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long userId = userDetails.getId();
        userService.deleteImage(userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_IMAGE_DELETED));
    }
}
