package caffeine.nest_dev.domain.user.controller;

import caffeine.nest_dev.common.dto.CommonResponse;
import caffeine.nest_dev.common.enums.SuccessCode;
import caffeine.nest_dev.domain.user.dto.request.UpdatePasswordRequestDto;
import caffeine.nest_dev.domain.user.dto.request.UserRequestDto;
import caffeine.nest_dev.domain.user.dto.response.UserResponseDto;
import caffeine.nest_dev.domain.user.entity.UserDetailsImpl;
import caffeine.nest_dev.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // 마이페이지 조회
    @GetMapping("/{userId}")
    public ResponseEntity<CommonResponse<UserResponseDto>> getUser(
            @PathVariable Long userId
    ) {

        UserResponseDto dto = userService.findById(userId);

        return ResponseEntity.ok().body(CommonResponse.of(SuccessCode.SUCCESS_FIND_USER, dto));
    }

    // 정보 수정
    @PatchMapping("/me")
    public ResponseEntity<CommonResponse<Void>> updateUser(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody UserRequestDto dto
    ) {

        userService.updateUser(userDetails.getUser(), dto);

        return ResponseEntity.ok(CommonResponse.of(SuccessCode.SUCCESS_UPDATE_USER, null));
    }

    // 비밀번호 수정
    @PatchMapping("/me/password")
    public ResponseEntity<CommonResponse<Void>> updatePassword(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody UpdatePasswordRequestDto dto
    ) {

        userService.updatePassword(userDetails.getUser(), dto);

        return ResponseEntity.ok(CommonResponse.of(SuccessCode.SUCCESS_UPDATE_PASSWORD, null));
    }
}
