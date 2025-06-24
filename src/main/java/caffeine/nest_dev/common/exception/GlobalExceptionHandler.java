package caffeine.nest_dev.common.exception;

import caffeine.nest_dev.common.dto.CommonResponse;
import caffeine.nest_dev.common.enums.BaseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<CommonResponse<Object>> handleBaseException(BaseException e) {
        log.warn(e.getMessage());
        e.printStackTrace(); // 에러 로그 띄우기
        BaseCode errorCode = e.getErrorCode();
        return ResponseEntity.status(errorCode.getStatus())
                .body(CommonResponse.of(errorCode));
    }

    // SSE 연결 끊김 예외 처리 (로그만 남기고 무시)
    @ExceptionHandler(AsyncRequestNotUsableException.class)
    public void handleAsyncRequestNotUsableException(AsyncRequestNotUsableException e) {
        log.warn("Servlet container error notification for disconnected client");
        // SSE 연결이 끊어진 경우이므로 응답을 보낼 수 없음. 로그만 남김.
    }

    // Optionally → 모든 예외 (예상 못한 500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<Object>> handleException(Exception e) {
        log.warn(e.getMessage());
        
        e.printStackTrace(); // 에러 로그 띄우기
        return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CommonResponse.of(caffeine.nest_dev.common.enums.ErrorCode.INTERNAL_SERVER_ERROR));
    }
}