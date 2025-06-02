package caffeine.nest_dev.common.enums;

import org.springframework.http.HttpStatus;

public enum ErrorCode implements BaseCode {

    // Auth
    UNAUTHORIZED_ROLE(HttpStatus.FORBIDDEN, "권한이 없는 유저입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자입니다."),
    NO_PERMISSION(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    IS_BLACKLISTED(HttpStatus.UNAUTHORIZED, "로그아웃 된 토큰입니다."),

    // Ticket
    NOT_FOUND_TICKET(HttpStatus.NOT_FOUND, "이용권이 없습니다."),

    // SERVER_ERROR
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
