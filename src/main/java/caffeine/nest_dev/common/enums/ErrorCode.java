package caffeine.nest_dev.common.enums;

import org.springframework.http.HttpStatus;

public enum ErrorCode implements BaseCode {
    // Auth
    UNAUTHORIZED_ROLE(HttpStatus.FORBIDDEN, "권한이 없는 유저입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자입니다."),
    NO_PERMISSION(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    IS_BLACKLISTED(HttpStatus.UNAUTHORIZED, "사용할 수 없는 토큰입니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    TOKEN_MISSING(HttpStatus.UNAUTHORIZED, "토큰이 전달되지 않았습니다."),
    TOKEN_USER_MISMATCH(HttpStatus.UNAUTHORIZED, "토큰의 사용자 정보가 일치하지 않습니다."),

    // User
    NOT_FOUND_USER(HttpStatus.BAD_REQUEST, "유저가 존재하지 않습니다."),
    ALREADY_EXIST_EMAIL(HttpStatus.BAD_REQUEST, "중복된 이메일입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),

    // Ticket
    NOT_FOUND_TICKET(HttpStatus.NOT_FOUND, "이용권이 없습니다."),


    // user
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    ACCESS_DENIED(HttpStatus.UNAUTHORIZED, "접근 가능한 사용자가 아닙니다."),

    // Complaint
    ERROR_CREATE_COMPLAINT(HttpStatus.CREATED, "민원이 생성되었습니다."),
    COMPLAINT_NEED_RESERVATION_ID(HttpStatus.BAD_REQUEST, "예약 ID가 없습니다."),
    COMPLAINT_NOT_FOUND(HttpStatus.NOT_FOUND, "민원을 찾을 수 없습니다."),

    // AdminCoupon
    NOT_FOUND_ADMIN_COUPON(HttpStatus.NOT_FOUND, "쿠폰이 없습니다."),
    COUPON_QUANTITY_NOT_SET(HttpStatus.INTERNAL_SERVER_ERROR, "쿠폰 수량 정보가 존재하지 않습니다."),
    COUPON_OUT_OF_STOCK(HttpStatus.CONFLICT, "쿠폰이 모두 소진되었습니다."),
    NOT_FOUND_COUPON(HttpStatus.NOT_FOUND, "존재하지 않는 쿠폰입니다."),

    // UserCoupon
    NOT_FOUND_USER_COUPON(HttpStatus.NOT_FOUND, "보유하신 쿠폰이 없습니다."),

    // SERVER_ERROR
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"),

    // Admin 도메인 에러 예시
    ADMIN_MENTOR_CAREER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 멘토 경력 요청입니다."),
    ALREADY_SAME_STATUS(HttpStatus.CONFLICT, "이미 되어있는 상태입니다."),

    // category
    CATEGORY_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 생성되어있는 카테고리입니다."),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 카테고리입니다."),
    ALREADY_SAME_CATEGORY_NAME(HttpStatus.CONFLICT, "같은 카테고리명 입니다."),
    ALREADY_EXIST_CATEGORY(HttpStatus.BAD_REQUEST, "중복된 카테고리 이름입니다."),

    // Reservation
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "예약을 찾을 수 없습니다."),
    RESERVATION_NOT_COMPLETED(HttpStatus.BAD_REQUEST, "예약한 상담이 완료되지 않았습니다."),

    // Review
    REVIEW_ALREADY_EXISTS(HttpStatus.CONFLICT, "리뷰가 이미 존재합니다."),
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "리뷰를 찾을 수 없습니다."),

    // Keyword
    KEYWORD_ALREADY_EXISTS(HttpStatus.CONFLICT, "중복된 키워드 이름입니다."),
    KEYWORD_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 카테고리입니다."),
    ALREADY_SAME_KEYWORD_NAME(HttpStatus.CONFLICT, "같은 키워드명 입니다."),

    // Profile
    NOT_FOUND_PROFILE(HttpStatus.BAD_REQUEST, "프로필이 존재하지 않습니다."),

    // Career
    NOT_FOUND_CAREER(HttpStatus.BAD_REQUEST, "경력이 존재하지 않습니다."),
    CAREER_CERTIFICATE_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "경력 증명서는 최대 3개까지만 등록할 수 있습니다."),
    CAREER_CERTIFICATE_EMPTY(HttpStatus.BAD_REQUEST, "경력증명서는 반드시 필요합니다."),

    // Certificate
    NOT_FOUND_CERTIFICATE(HttpStatus.BAD_REQUEST, "경력증명서가 존재하지 않습니다."),


    // ChatRoom
    CHATROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "채팅방이 존재하지 않습니다."),
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
