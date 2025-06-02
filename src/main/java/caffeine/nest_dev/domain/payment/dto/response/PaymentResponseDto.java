package caffeine.nest_dev.domain.payment.dto.response;

import caffeine.nest_dev.domain.payment.entity.Payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PaymentResponseDto {

    private final Long id;
    private final Long reservationId;
    private final Long mentorId;
    private final Long menteeId;
    private final Long userCouponId;
    private final Long ticketId;
    private final Integer price;
    private final String paymentType;
    private final String paymentStatus;

    public static PaymentResponseDto of(Payment payment) {
        return PaymentResponseDto.builder()
                .id(payment.getId())
                .reservationId(payment.getReservation().getId())
                .mentorId(payment.getMentor().getId())
                .menteeId(payment.getMentee().getId())
                .menteeId(payment.getMentee().getId())
                .ticketId(payment.getTicket().getId())
                .userCouponId(payment.getId())
                .price(payment.getPrice())
                .paymentType(payment.getPaymentType().name())
                .paymentStatus(payment.getPaymentStatus().name())
                .build();
    }
}
