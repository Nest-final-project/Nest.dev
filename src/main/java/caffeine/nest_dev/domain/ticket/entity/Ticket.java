package caffeine.nest_dev.domain.ticket.entity;

import caffeine.nest_dev.common.entity.BaseEntity;
import caffeine.nest_dev.domain.payment.entity.Payment;
import caffeine.nest_dev.domain.ticket.enums.TicketTime;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@Getter
@Table(name = "tickets")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ticket extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer price;

    @Enumerated(EnumType.STRING)
    private TicketTime ticketTime;

    @Column(nullable = false)
    private String description;

    @OneToMany(mappedBy = "ticket")
    private List<Payment> payments = new ArrayList<>();
}


