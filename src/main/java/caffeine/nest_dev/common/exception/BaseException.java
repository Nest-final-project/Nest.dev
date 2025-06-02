package caffeine.nest_dev.common.exception;

import caffeine.nest_dev.common.enums.ErrorCode;

public class BaseException extends RuntimeException {

    private final ErrorCode errorCode;
    public BaseException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
