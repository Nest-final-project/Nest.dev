package caffeine.nest_dev.common.websocket.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class WebSocketErrorResponse {

    private final String error;
    private final String message;
    private final String code;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime timestamp;

    private final String sessionId;

    public static WebSocketErrorResponse of(String error, String message, String code, String sessionId) {
        return WebSocketErrorResponse.builder()
                .error(error)
                .message(message)
                .code(code)
                .sessionId(sessionId)
                .timestamp(LocalDateTime.now())
                .build();
    }

}
