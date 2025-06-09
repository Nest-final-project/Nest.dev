package caffeine.nest_dev.domain.auth.controller;

import caffeine.nest_dev.common.dto.CommonResponse;
import caffeine.nest_dev.common.enums.SuccessCode;
import caffeine.nest_dev.domain.auth.dto.request.AuthRequestDto;
import caffeine.nest_dev.domain.auth.dto.request.LoginRequestDto;
import caffeine.nest_dev.domain.auth.dto.request.LogoutRequestDto;
import caffeine.nest_dev.domain.auth.dto.request.RefreshTokenRequestDto;
import caffeine.nest_dev.domain.auth.dto.response.AuthResponseDto;
import caffeine.nest_dev.domain.auth.dto.response.LoginResponseDto;
import caffeine.nest_dev.domain.auth.dto.response.TokenResponseDto;
import caffeine.nest_dev.domain.auth.service.AuthService;
import caffeine.nest_dev.domain.user.entity.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<CommonResponse<AuthResponseDto>> signup(
            @Valid @RequestBody AuthRequestDto dto) {

        AuthResponseDto responseDto = authService.signup(dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.of(SuccessCode.SUCCESS_USER_SIGNUP, responseDto));
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<CommonResponse<LoginResponseDto>> login(
            @RequestBody LoginRequestDto dto) {

        LoginResponseDto responseDto = authService.login(dto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_USER_LOGIN, responseDto));
    }

    // 로그아웃
    @DeleteMapping("/logout")
    public ResponseEntity<CommonResponse<Void>> logout(
            @RequestHeader("Authorization") String accessToken,
            @RequestBody LogoutRequestDto dto
    ) {

        authService.logout(accessToken, dto.getRefreshToken());

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_USER_LOGOUT));
    }

    // 토큰 재발급
    @PostMapping("/refresh")
    public ResponseEntity<CommonResponse<TokenResponseDto>> reissue(
            @RequestBody RefreshTokenRequestDto dto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {

        TokenResponseDto responseDto = authService.reissue(dto, userDetails.getId());

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_REISSUE_TOKEN, responseDto));
    }
}
