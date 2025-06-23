package caffeine.nest_dev.domain.payment.dto.response;

import caffeine.nest_dev.domain.payment.entity.Payment;
import caffeine.nest_dev.domain.payment.enums.PaymentStatus;
import caffeine.nest_dev.domain.payment.enums.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class PaymentsResponseDto {

    private Long id;
    private String mentorName;
    private String ticketName;
    private Integer amount;
    private PaymentStatus status;
    private PaymentType paymentType;
    private String approvedAt;

    public static PaymentsResponseDto of(Payment payment, String mentorName, String ticketName) {
        return PaymentsResponseDto.builder()
                .id(payment.getId())
                .mentorName(mentorName)
                .ticketName(ticketName)
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .paymentType(payment.getPaymentType())
                .approvedAt(payment.getApprovedAt())
                .build();
    }
}
