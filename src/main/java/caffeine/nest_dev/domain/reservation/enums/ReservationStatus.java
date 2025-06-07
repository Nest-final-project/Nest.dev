package caffeine.nest_dev.domain.reservation.enums;

public enum ReservationStatus {
    REQUESTED, // 상담 요청
    CONFIRMED, // 승인 완료
    PAID, // 결제 완료
    COMPLETED,   // 상담 정상 종료
    CANCELED, // 예약 취소
    NO_SHOW // 노쇼
}
