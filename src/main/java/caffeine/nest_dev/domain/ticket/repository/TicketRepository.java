package caffeine.nest_dev.domain.ticket.repository;

import caffeine.nest_dev.domain.ticket.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
}
