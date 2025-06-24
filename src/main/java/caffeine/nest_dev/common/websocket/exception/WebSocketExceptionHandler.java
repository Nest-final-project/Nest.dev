package caffeine.nest_dev.common.websocket.exception;

import caffeine.nest_dev.common.dto.CommonResponse;
import caffeine.nest_dev.common.enums.BaseCode;
import caffeine.nest_dev.common.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class WebSocketExceptionHandler {

    @MessageExceptionHandler(BaseException.class)
    @SendToUser("/queue/errors")
    public CommonResponse<Object> handleSocketException(BaseException e) {
        log.warn(e.getMessage());
        BaseCode errorCode = e.getErrorCode();
        return CommonResponse.of(errorCode);
    }

}

