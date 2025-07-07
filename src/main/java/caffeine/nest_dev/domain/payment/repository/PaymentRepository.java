package caffeine.nest_dev.domain.payment.repository;

import caffeine.nest_dev.domain.payment.entity.Payment;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByReservationId(Long reservationId);

    @Query("SELECT p FROM Payment p " +
            "LEFT JOIN FETCH p.reservation r " +
            "LEFT JOIN FETCH r.mentor u " + // Reservation 엔티티에 mentor 필드가 User 엔티티와 연결되어 있다고 가정
            "LEFT JOIN FETCH p.ticket t " +
            "WHERE p.payer.id = :payerId")
    Page<Payment> findAllByPayerIdWithDetails(@Param("payerId") Long payerId, Pageable pageable);
}
