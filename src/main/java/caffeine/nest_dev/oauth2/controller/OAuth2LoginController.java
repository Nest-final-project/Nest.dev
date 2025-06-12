package caffeine.nest_dev.oauth2.controller;


import caffeine.nest_dev.common.dto.CommonResponse;
import caffeine.nest_dev.common.enums.SuccessCode;
import caffeine.nest_dev.domain.user.enums.SocialType;
import caffeine.nest_dev.oauth2.dto.response.OAuth2LoginResponseDto;
import caffeine.nest_dev.oauth2.service.OAuth2LoginService;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OAuth2LoginController {

    private final OAuth2LoginService oAuth2LoginService;

    // 로그인 페이지 요청
    @GetMapping("/oauth2/login/{provider}")
    public void redirectLoginPage(
            @PathVariable SocialType provider,
            HttpServletResponse response
    ) throws IOException {

        // socialType에 해당하는 로그인 페이지 URL 반환
        String loginPageUrl = oAuth2LoginService.generateLoginPageUrl(provider);

        // 로그인 페이지로 이동
        response.sendRedirect(loginPageUrl);
    }

    // 소셜 로그인
    @GetMapping("/oauth2/callback/{provider}")
    public ResponseEntity<CommonResponse<OAuth2LoginResponseDto>> oauth2Login(
            @PathVariable SocialType provider,
            @RequestParam("code") String authorizationCode,
            @RequestParam("state") String state
    ) {

        OAuth2LoginResponseDto responseDto = oAuth2LoginService.login(provider, authorizationCode,
                state);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_USER_LOGIN, responseDto));
    }
}
