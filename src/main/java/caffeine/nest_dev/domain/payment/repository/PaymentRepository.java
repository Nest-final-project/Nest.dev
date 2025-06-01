package caffeine.nest_dev.domain.payment.repository;

import caffeine.nest_dev.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
