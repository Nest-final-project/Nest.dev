package caffeine.nest_dev.common.websocket.controller;

import caffeine.nest_dev.common.dto.CommonResponse;
import caffeine.nest_dev.common.enums.SuccessCode;
import caffeine.nest_dev.common.websocket.dto.SocketTokenResponseDto;
import caffeine.nest_dev.common.websocket.service.SocketTokenService;
import caffeine.nest_dev.domain.user.entity.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Socket Token", description = "WebSocket 토큰 관리 API")
@RestController
@RequiredArgsConstructor
public class SocketTokenController {

    private final SocketTokenService socketTokenService;

    // socket token 발급
    @Operation(summary = "WebSocket 토큰 발급", description = "WebSocket 연결을 위한 토큰을 발급합니다")
    @ApiResponse(responseCode = "201", description = "WebSocket 토큰 발급 성공")
    @PostMapping("/socket/token")
    public ResponseEntity<CommonResponse<SocketTokenResponseDto>> createSocketToken(
            @Parameter(description = "인증된 사용자 정보") @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long userId = userDetails.getId();
        SocketTokenResponseDto responseDto = socketTokenService.createSocketToken(userId);
        return ResponseEntity.created(URI.create("socket/token"))
                .body(CommonResponse.of(SuccessCode.SUCCESS_SOCKET_TOKEN_CREATED, responseDto));
    }
}
