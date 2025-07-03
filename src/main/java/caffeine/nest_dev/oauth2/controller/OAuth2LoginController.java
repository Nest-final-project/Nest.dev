package caffeine.nest_dev.oauth2.controller;


import caffeine.nest_dev.common.dto.CommonResponse;
import caffeine.nest_dev.common.enums.SuccessCode;
import caffeine.nest_dev.domain.user.enums.SocialType;
import caffeine.nest_dev.oauth2.dto.response.OAuth2LoginResponseDto;
import caffeine.nest_dev.oauth2.service.OAuth2LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "OAuth2", description = "소셜 로그인 API")
@RestController
@RequiredArgsConstructor
public class OAuth2LoginController {

    private final OAuth2LoginService oAuth2LoginService;

    // 로그인 페이지 요청
    @Operation(summary = "소셜 로그인 페이지 리다이렉트", description = "소셜 로그인 페이지로 리다이렉트합니다")
    @ApiResponse(responseCode = "200", description = "로그인 페이지 리다이렉트 성공")
    @GetMapping("/oauth2/login/{provider}")
    public void redirectLoginPage(
            @Parameter(description = "소셜 로그인 제공자") @PathVariable SocialType provider,
            @Parameter(description = "HTTP 응답 객체") HttpServletResponse response
    ) throws IOException {

        // socialType에 해당하는 로그인 페이지 URL 반환
        String loginPageUrl = oAuth2LoginService.generateLoginPageUrl(provider);

        // 로그인 페이지로 이동
        response.sendRedirect(loginPageUrl);
    }

    // 소셜 로그인
    @Operation(summary = "소셜 로그인 콜백 처리", description = "소셜 로그인 인증 코드를 처리합니다")
    @ApiResponse(responseCode = "200", description = "소셜 로그인 콜백 처리 성공")
    @GetMapping("/oauth2/callback/{provider}")
    public void oauth2Login(
            @Parameter(description = "소셜 로그인 제공자") @PathVariable SocialType provider,
            @Parameter(description = "인증 코드") @RequestParam("code") String authorizationCode,
            @Parameter(description = "상태 값") @RequestParam("state") String state,
            @Parameter(description = "HTTP 응답 객체") HttpServletResponse response
    ) throws IOException {

        String url = oAuth2LoginService.login(provider, authorizationCode,
                state);

        response.sendRedirect(url);
    }

    // 소셜 로그인 정보 확인
    @Operation(summary = "소셜 로그인 정보 확인", description = "소셜 로그인 결과를 확인합니다")
    @ApiResponse(responseCode = "200", description = "소셜 로그인 정보 확인 성공")
    @GetMapping("/oauth2/callback")
    public ResponseEntity<CommonResponse<OAuth2LoginResponseDto>> oauth2LoginCheck(
            @Parameter(description = "확인 코드") @RequestParam("code") String code
    ) {
        OAuth2LoginResponseDto dto = oAuth2LoginService.loginCheck(code);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_USER_LOGIN, dto));
    }
}
