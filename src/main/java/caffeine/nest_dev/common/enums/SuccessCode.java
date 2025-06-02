package caffeine.nest_dev.common.enums;

import org.springframework.http.HttpStatus;

public enum SuccessCode implements BaseCode{
    // Auth
    SUCCESS_USER_LOGIN(HttpStatus.OK,"로그인을 성공하였습니다."),
    SUCCESS_USER_LOGOUT(HttpStatus.OK,"로그아웃 되었습니다."),
    SUCCESS_USER_SIGNUP(HttpStatus.CREATED, "회원가입에 성공하였습니다."),

    // User
    SUCCESS_FIND_USER(HttpStatus.OK, "유저 조회에 성공했습니다."),
    SUCCESS_UPDATE_USER(HttpStatus.OK, "정보가 수정되었습니다."),
    SUCCESS_UPDATE_PASSWORD(HttpStatus.OK, "비밀번호 변경이 성공되었습니다."),

    // Ticket
    SUCCESS_TICKET_CREATED(HttpStatus.CREATED, "이용권에 등록을 성공하였습니다."),
    SUCCESS_TICKET_READ(HttpStatus.OK, "이용권을 조회하였습니다."),
    SUCCESS_TICKET_UPDATED(HttpStatus.OK, "이용권이 수정되었습니다."),
    SUCCESS_TICKET_DELETED(HttpStatus.NO_CONTENT, "이용권이 삭제되었습니다."),

    // Admin
    SUCCESS_ADMIN_MENTOR_CAREER_READ(HttpStatus.OK, "멘토 경력 확인 요청 목록을 조회하였습니다."),
    SUCCESS_ADMIN_MENTOR_CAREER_DETAIL_READ(HttpStatus.OK, "관리자 멘토 경력 단건 조회 성공"),
    SUCCESS_ADMIN_MENTOR_CAREER_STATUS_UPDATED(HttpStatus.OK, "멘토 경력 요청 상태가 변경되었습니다."),

    ;



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
