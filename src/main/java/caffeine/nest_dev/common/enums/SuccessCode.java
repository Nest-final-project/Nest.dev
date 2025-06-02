package caffeine.nest_dev.common.enums;

import org.springframework.http.HttpStatus;

public enum SuccessCode implements BaseCode{
    // Auth
    SUCCESS_USER_LOGIN(HttpStatus.OK,"로그인을 성공하였습니다."),
    SUCCESS_USER_LOGOUT(HttpStatus.OK,"로그아웃 되었습니다."),
    SUCCESS_USER_SIGNUP(HttpStatus.CREATED, "회원가입에 성공하였습니다."),

    // Ticket
    SUCCESS_TICKET_CREATED(HttpStatus.CREATED, "이용권에 등록을 성공하였습니다."),
    SUCCESS_TICKET_READ(HttpStatus.OK, "이용권을 조회하였습니다."),
    SUCCESS_TICKET_UPDATED(HttpStatus.OK, "이용권이 수정되었습니다."),
    SUCCESS_TICKET_DELETED(HttpStatus.NO_CONTENT, "이용권이 삭제되었습니다."),


    // Complaints
    SUCCESS_CREATE_COMPLAINT(HttpStatus.CREATED, "민원이 생성되었습니다."),


    // Review
    SUCCESS_CREATE_REVIEW(HttpStatus.CREATED, "리뷰가 생성되었습니다."),
    SUCCESS_SHOW_REVIEWS(HttpStatus.OK, "리뷰 목록이 조회되었습니다."),
    SUCCESS_UPDATE_REVIEW(HttpStatus.OK, "리뷰 수정에 성공했습니다."),
    SUCESSS_DELETE_REVIEW(HttpStatus.OK, "리뷰 삭제에 성공했습니다.");

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
