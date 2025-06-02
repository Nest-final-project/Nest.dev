package caffeine.nest_dev.common.enums;

import org.springframework.http.HttpStatus;

public enum ErrorCode implements BaseCode {
    // Auth
    UNAUTHORIZED_ROLE(HttpStatus.FORBIDDEN, "권한이 없는 유저입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자입니다."),
    NO_PERMISSION(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    IS_BLACKLISTED(HttpStatus.UNAUTHORIZED, "로그아웃 된 토큰입니다."),
    ERROR_USER_LOGIN(HttpStatus.OK,"로그인을 실패하였습니다."),
    ERROR_USER_LOGOUT(HttpStatus.OK,"로그아웃을 실패하였습니다."),

    // Ticket
    NOT_FOUND_TICKET(HttpStatus.NOT_FOUND, "이용권이 없습니다."),


    // user
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    ACCESS_DENIED(HttpStatus.UNAUTHORIZED, "접근 가능한 사용자가 아닙니다."),

        // Complaint
        ERROR_CREATE_COMPLAINT(HttpStatus.CREATED, "민원이 생성되었습니다."),


        // Reservation
        RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "예약을 찾을 수 없습니다."),


        // Review
        REVIEW_ALREADY_EXISTS(HttpStatus.CONFLICT, "리뷰가 이미 존재합니다."),
        REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "리뷰를 찾을 수 없습니다.");

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

    public String getMessage(Object... args) {
        return message;
    }

}
