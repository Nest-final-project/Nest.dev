package caffeine.nest_dev.domain.websocket.service;

import caffeine.nest_dev.common.enums.ErrorCode;
import caffeine.nest_dev.common.exception.BaseException;
import caffeine.nest_dev.domain.user.entity.User;
import caffeine.nest_dev.domain.user.repository.UserRepository;
import caffeine.nest_dev.domain.websocket.dto.response.SocketTokenResponseDto;
import caffeine.nest_dev.domain.websocket.util.SocketJwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SocketTokenService {

    private final SocketJwtUtil socketJwtUtil;
    private final UserRepository userRepository;

    public SocketTokenResponseDto createSocketToken(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new BaseException(ErrorCode.USER_NOT_FOUND)
        );

        // 소켓 토큰 생성
        String socketToken = socketJwtUtil.createSocketToken(user);

        return SocketTokenResponseDto.of(socketToken);
    }
}
