package caffeine.nest_dev.common.enums;

import org.springframework.http.HttpStatus;

public enum SuccessCode implements BaseCode {
    // Auth
    SUCCESS_USER_LOGIN(HttpStatus.OK, "로그인을 성공하였습니다."),
    SUCCESS_USER_LOGOUT(HttpStatus.OK, "로그아웃 되었습니다."),
    SUCCESS_USER_SIGNUP(HttpStatus.CREATED, "회원가입을 성공하였습니다."),
    SUCCESS_REISSUE_TOKEN(HttpStatus.OK, "토큰을 재발행합니다."),

    // User
    SUCCESS_FIND_USER(HttpStatus.OK, "상세페이지 조회를 성공하였습니다."),
    SUCCESS_UPDATE_USER(HttpStatus.OK, "정보 수정을 성공하였습니다.."),
    SUCCESS_UPDATE_PASSWORD(HttpStatus.OK, "비밀번호 수정을 성공하였습니다."),
    SUCCESS_DELETE_USER(HttpStatus.OK, "회원 탈퇴가 완료되었습니다."),

    // Ticket
    SUCCESS_TICKET_CREATED(HttpStatus.CREATED, "이용권에 등록을 성공하였습니다."),
    SUCCESS_TICKET_READ(HttpStatus.OK, "이용권을 조회하였습니다."),
    SUCCESS_TICKET_UPDATED(HttpStatus.OK, "이용권이 수정되었습니다."),
    SUCCESS_TICKET_DELETED(HttpStatus.OK, "이용권이 삭제되었습니다."),

    // AdminCoupon
    SUCCESS_ADMIN_COUPON_CREATED(HttpStatus.CREATED, "쿠폰 생성을 성공하였습니다."),
    SUCCESS_ADMIN_COUPON_READ(HttpStatus.OK, "쿠폰 목록을 조회 완료하였습니다."),
    SUCCESS_ADMIN_COUPON_UPDATED(HttpStatus.OK, "쿠폰이 수정되었습니다."),
    SUCCESS_ADMIN_COUPON_DELETED(HttpStatus.OK, "쿠폰이 삭제되었습니다."),

    // UserCoupon
    SUCCESS_USER_COUPON_CREATED(HttpStatus.CREATED, "쿠폰 발급이 완료되었습니다."),
    SUCCESS_USER_COUPON_READ(HttpStatus.OK, "목록을 조회 완료하였습니다."),
    SUCCESS_USER_COUPON_UPDATED(HttpStatus.OK, "쿠폰이 사용되었습니다."),

    // Admin
    SUCCESS_ADMIN_MENTOR_CAREER_READ(HttpStatus.OK, "멘토 경력 확인 요청 목록을 조회하였습니다."),
    SUCCESS_ADMIN_MENTOR_CAREER_DETAIL_READ(HttpStatus.OK, "관리자 멘토 경력 단건 조회 성공"),
    SUCCESS_ADMIN_MENTOR_CAREER_STATUS_UPDATED(HttpStatus.OK, "멘토 경력 요청 상태가 변경되었습니다."),

    // ChatRoom
    SUCCESS_CHATROOM_CREATED(HttpStatus.CREATED, "채팅방이 생성되었습니다."),
    SUCCESS_CHATROOM_READ(HttpStatus.OK, "채팅방 목록이 조회되었습니다."),

    // Complaints
    SUCCESS_CREATE_COMPLAINT(HttpStatus.CREATED, "민원이 생성되었습니다."),
    SUCCESS_SHOW_COMPLAINTS(HttpStatus.OK, "민원 목록이 조회되었습니다."),
    SUCCESS_SHOW_COMPLAINT(HttpStatus.OK, "민원 상세 내용 조회에 성공하였습니다."),

    // category
    SUCCESS_CATEGORY_CREATED(HttpStatus.CREATED, "카테고리가 생성되었습니다."),
    SUCCESS_CATEGORY_READ(HttpStatus.OK, "카테고리 목록 조회 성공"),
    SUCCESS_CATEGORY_UPDATED(HttpStatus.OK, "카테고리가 수정되었습니다."),
    SUCCESS_CATEGORY_DELETED(HttpStatus.OK, "카테고리가 삭제되었습니다."),


    // Review
    SUCCESS_CREATE_REVIEW(HttpStatus.CREATED, "리뷰가 생성되었습니다."),
    SUCCESS_SHOW_REVIEWS(HttpStatus.OK, "리뷰 목록이 조회되었습니다."),
    SUCCESS_UPDATE_REVIEW(HttpStatus.OK, "리뷰 수정에 성공했습니다."),
    SUCCESS_DELETE_REVIEW(HttpStatus.OK, "리뷰 삭제에 성공했습니다."),
    SUCCESS_ACTIVE_REVIEW(HttpStatus.OK, "리뷰 복구에 성공했습니다."),

    // Career
    SUCCESS_CAREER_CREATED(HttpStatus.CREATED, "경력이 생성되었습니다."),
    SUCCESS_CAREER_READ(HttpStatus.OK, "경력 상세 페이지 조회가 완료되었습니다."),
    SUCCESS_CAREERS_READ(HttpStatus.OK, "경력 목록이 조회되었습니다."),
    SUCCESS_CAREER_UPDATED(HttpStatus.OK, "경력 수정이 완료되었습니다."),
    SUCCESS_CAREER_DELETED(HttpStatus.OK, "경력 삭제가 완료되었습니다."),

    // Certificate
    SUCCESS_CERTIFICATE_UPDATED(HttpStatus.OK, "경력증명서 수정이 완료되었습니다."),
    SUCCESS_CERTIFICATE_DELETED(HttpStatus.OK, "경력증명서 삭제가 완료되었습니다.")

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
