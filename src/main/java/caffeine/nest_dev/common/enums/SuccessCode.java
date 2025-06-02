package caffeine.nest_dev.common.enums;

import org.springframework.http.HttpStatus;

public enum SuccessCode implements BaseCode{
    // Auth
    SUCCESS_USER_LOGIN(HttpStatus.OK,"로그인을 성공하였습니다."),
    SUCCESS_USER_LOGOUT(HttpStatus.OK,"로그아웃 되었습니다."),

    // Ticket
    SUCCESS_TICKET_CREATED(HttpStatus.CREATED, "이용권에 등록을 성공하였습니다."),
    SUCCESS_TICKET_READ(HttpStatus.OK, "이용권을 조회하였습니다."),


    // Complaints
    SUCCESS_CREATE_COMPLAINT(HttpStatus.CREATED, "민원이 생성되었습니다."),


    // Review
    SUCCESS_CREATE_REIVEW(HttpStatus.CREATED, "리뷰가 생성되었습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    SuccessCode(HttpStatus httpStatus, String message) {
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
