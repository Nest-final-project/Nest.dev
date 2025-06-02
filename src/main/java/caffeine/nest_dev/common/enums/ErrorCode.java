package caffeine.nest_dev.common.enums;

import org.springframework.http.HttpStatus;

public enum ErrorCode implements BaseCode{

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"),

    // Admin 도메인 에러 예시
    ADMIN_MENTOR_CAREER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 멘토 경력 요청입니다."),
    ALREADY_SAME_STATUS(HttpStatus.CONFLICT, "이미 되어있는 상태입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public HttpStatus getStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
