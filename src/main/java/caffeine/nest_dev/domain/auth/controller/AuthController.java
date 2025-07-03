package caffeine.nest_dev.domain.auth.controller;

import caffeine.nest_dev.common.dto.CommonResponse;
import caffeine.nest_dev.common.enums.SuccessCode;
import caffeine.nest_dev.domain.auth.dto.request.AuthCodeRequestDto;
import caffeine.nest_dev.domain.auth.dto.request.AuthRequestDto;
import caffeine.nest_dev.domain.auth.dto.request.EmailAuthRequestDto;
import caffeine.nest_dev.domain.auth.dto.request.LoginRequestDto;
import caffeine.nest_dev.domain.auth.dto.request.LogoutRequestDto;
import caffeine.nest_dev.domain.auth.dto.request.RefreshTokenRequestDto;
import caffeine.nest_dev.domain.auth.dto.response.AuthResponseDto;
import caffeine.nest_dev.domain.auth.dto.response.LoginResponseDto;
import caffeine.nest_dev.domain.auth.dto.response.TokenResponseDto;
import caffeine.nest_dev.domain.auth.service.AuthService;
import caffeine.nest_dev.domain.user.entity.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Auth", description = "인증 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    // 회원가입
    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다")
    @ApiResponse(responseCode = "201", description = "회원가입 성공")
    @PostMapping("/signup")
    public ResponseEntity<CommonResponse<AuthResponseDto>> signup(
            @Parameter(description = "회원가입 요청 정보") @Valid @RequestBody AuthRequestDto dto
    ) {

        AuthResponseDto responseDto = authService.signup(dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.of(SuccessCode.SUCCESS_USER_SIGNUP, responseDto));
    }

    // 인증코드 보내기
    @Operation(summary = "인증코드 발송", description = "이메일로 인증코드를 발송합니다")
    @ApiResponse(responseCode = "200", description = "인증코드 발송 성공")
    @PostMapping("/signup/code")
    public ResponseEntity<CommonResponse<Void>> signupCode(
            @Parameter(description = "이메일 인증 요청 정보") @Valid @RequestBody EmailAuthRequestDto dto
    ) {

        authService.signupCode(dto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_CODE_SEND));
    }

    // 입력받은 인증코드 검증
    @Operation(summary = "인증코드 검증", description = "입력받은 인증코드를 검증합니다")
    @ApiResponse(responseCode = "200", description = "인증코드 검증 성공")
    @PostMapping("/signup/code/verify")
    public ResponseEntity<CommonResponse<Void>> verifyCode(
            @Parameter(description = "인증코드 검증 요청 정보") @RequestBody AuthCodeRequestDto dto
    ) {

        authService.verifyCode(dto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_CODE_VERIFY));
    }

    // 로그인
    @Operation(summary = "로그인", description = "사용자 로그인을 처리합니다")
    @ApiResponse(responseCode = "200", description = "로그인 성공")
    @PostMapping("/login")
    public ResponseEntity<CommonResponse<LoginResponseDto>> login(
            @Parameter(description = "로그인 요청 정보") @RequestBody LoginRequestDto dto) {

        LoginResponseDto responseDto = authService.login(dto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_USER_LOGIN, responseDto));
    }

    // 로그아웃
    @Operation(summary = "로그아웃", description = "사용자 로그아웃을 처리합니다")
    @ApiResponse(responseCode = "200", description = "로그아웃 성공")
    @DeleteMapping("/logout")
    public ResponseEntity<CommonResponse<Void>> logout(
            @Parameter(description = "JWT 액세스 토큰") @RequestHeader("Authorization") String accessToken,
            @Parameter(description = "로그아웃 요청 정보") @RequestBody LogoutRequestDto dto
    ) {

        authService.logout(accessToken, dto.getRefreshToken());

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_USER_LOGOUT));
    }

    // 토큰 재발급
    @Operation(summary = "토큰 재발급", description = "리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급합니다")
    @ApiResponse(responseCode = "200", description = "토큰 재발급 성공")
    @PostMapping("/token/refresh")
    public ResponseEntity<CommonResponse<TokenResponseDto>> reissue(
            @Parameter(description = "리프레시 토큰 요청 정보") @RequestBody RefreshTokenRequestDto dto,
            @Parameter(description = "인증된 사용자 정보") @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {

        TokenResponseDto responseDto = authService.reissue(dto, userDetails.getId());

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_REISSUE_TOKEN, responseDto));
    }
}
