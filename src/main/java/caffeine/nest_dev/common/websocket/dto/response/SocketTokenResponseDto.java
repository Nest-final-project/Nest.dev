package caffeine.nest_dev.common.websocket.dto.response;

import lombok.Getter;

@Getter
public class SocketTokenResponseDto {

    private final String token;

    private SocketTokenResponseDto(String token) {
        this.token = token;
    }

    public static SocketTokenResponseDto of(String token) {
        return new SocketTokenResponseDto(token);
    }
}
