package caffeine.nest_dev.common.exception;

import caffeine.nest_dev.common.dto.CommonResponse;
import caffeine.nest_dev.common.enums.BaseCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<CommonResponse<Object>> handleBaseException(BaseException e) {
        BaseCode errorCode = e.getErrorCode();
        return ResponseEntity.status(errorCode.getStatus())
                .body(CommonResponse.of(errorCode));
    }

    // Optionally → 모든 예외 (예상 못한 500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<Object>> handleException(Exception e) {
        return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CommonResponse.of(caffeine.nest_dev.common.enums.ErrorCode.INTERNAL_SERVER_ERROR));
    }
}