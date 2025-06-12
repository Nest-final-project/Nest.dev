package caffeine.nest_dev.common.websocket.service;

import caffeine.nest_dev.common.websocket.dto.response.SocketTokenResponseDto;
import caffeine.nest_dev.common.websocket.util.SocketJwtUtil;
import caffeine.nest_dev.domain.user.entity.User;
import caffeine.nest_dev.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SocketTokenService {

    private final SocketJwtUtil socketJwtUtil;
    private final UserService userService;

    public SocketTokenResponseDto createSocketToken(Long userId) {
        User user = userService.findByIdAndIsDeletedFalseOrElseThrow(userId);

        // 소켓 토큰 생성
        String socketToken = socketJwtUtil.createSocketToken(user);

        return SocketTokenResponseDto.of(socketToken);
    }
}
