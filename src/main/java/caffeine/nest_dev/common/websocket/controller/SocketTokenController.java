package caffeine.nest_dev.common.websocket.controller;

import caffeine.nest_dev.common.dto.CommonResponse;
import caffeine.nest_dev.common.enums.SuccessCode;
import caffeine.nest_dev.common.websocket.dto.response.SocketTokenResponseDto;
import caffeine.nest_dev.common.websocket.service.SocketTokenService;
import caffeine.nest_dev.domain.user.entity.UserDetailsImpl;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SocketTokenController {

    private final SocketTokenService socketTokenService;

    // socket token 발급
    @PostMapping("/socket/token")
    public ResponseEntity<CommonResponse<SocketTokenResponseDto>> createSocketToken(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long userId = userDetails.getId();
        SocketTokenResponseDto responseDto = socketTokenService.createSocketToken(userId);
        return ResponseEntity.created(URI.create("socket/token"))
                .body(CommonResponse.of(SuccessCode.SUCCESS_SOCKET_TOKEN_CREATED, responseDto));
    }
}
