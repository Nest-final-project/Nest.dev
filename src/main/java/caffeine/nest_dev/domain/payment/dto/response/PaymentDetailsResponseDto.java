package caffeine.nest_dev.domain.payment.dto.response;

import caffeine.nest_dev.domain.payment.entity.Payment;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class PaymentDetailsResponseDto {

    private String reservationId;       // 예약 고유 식별자
    private String ticketName;          // 이용권 이름
    private String payerName;           // 결제자 이름
    private String paymentKey;          // 고유 결제 key 값
    private String paymentStatus;       // 결제 처리 상태
    private String paymentMethod;       // 결제 수단
    private Integer amount;             // 결제 금액
    private String approvedAt;          // 결제 승인 시각

    public static PaymentDetailsResponseDto from(Payment payment) {
        return PaymentDetailsResponseDto.builder()
                .reservationId(String.valueOf(payment.getReservation().getId()))
                .ticketName(payment.getTicket().getName())
                .payerName(payment.getPayer().getName())
                .paymentKey(payment.getPaymentKey())
                .paymentStatus(payment.getStatus().getDescription())
                .paymentMethod(
                        payment.getTossPaymentMethod() != null ? payment.getTossPaymentMethod()
                                .name() : "N/A")
                .amount(payment.getAmount())
                .approvedAt(payment.getApprovedAt())
                .build();
    }
}