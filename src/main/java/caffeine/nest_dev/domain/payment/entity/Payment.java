package caffeine.nest_dev.domain.payment.entity;


import caffeine.nest_dev.domain.payment.enums.PaymentStatus;
import caffeine.nest_dev.domain.payment.enums.PaymentType;
import caffeine.nest_dev.domain.payment.enums.TossPaymentMethod;
import caffeine.nest_dev.domain.reservation.entity.Reservation;
import caffeine.nest_dev.domain.ticket.entity.Ticket;
import caffeine.nest_dev.domain.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "payments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String paymentKey;

    private Integer amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Enumerated(value = EnumType.STRING)
    private TossPaymentMethod tossPaymentMethod;

    private String requestedAt;

    private String approvedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false, unique = true)
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User payer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id") // Ticket도 관계에 추가
    private Ticket ticket;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    private String cancelReason;
    private String canceledAt;

    @Builder
    public Payment(Reservation reservation, Integer amount, PaymentStatus status, User payer,
            Ticket ticket, PaymentType paymentType, String requestedAt) {
        this.reservation = reservation;
        this.amount = amount;
        this.status = status;
        this.payer = payer;
        this.ticket = ticket;
        this.paymentType = paymentType;
        this.requestedAt = requestedAt;
    }

    public void updateOnSuccess(String paymentKey, TossPaymentMethod tossPaymentMethod,
            String approvedAt, String requestedAt) {
        this.paymentKey = paymentKey;
        this.tossPaymentMethod = tossPaymentMethod;
        this.status = PaymentStatus.PAID;
        this.approvedAt = approvedAt;
        this.requestedAt = requestedAt;
    }

    public void updateOnFailure() {
        this.status = PaymentStatus.FAILED;
    }

    public void updateOnCancel(String reason) {
        this.status = PaymentStatus.CANCELED;
        this.cancelReason = reason;
        this.canceledAt = LocalDateTime.now().toString(); // 취소된 시점 기록
    }
}