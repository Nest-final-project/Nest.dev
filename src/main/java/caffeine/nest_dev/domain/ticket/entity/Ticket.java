package caffeine.nest_dev.domain.ticket.entity;

import caffeine.nest_dev.common.entity.BaseEntity;
import caffeine.nest_dev.domain.ticket.dto.request.TicketRequestDto;
import caffeine.nest_dev.domain.ticket.enums.TicketTime;
import jakarta.persistence.*;
import lombok.*;

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

    public void modifyTicket(TicketRequestDto requestDto) {
        if (requestDto.getName() != null) {
            this.name = requestDto.getName();
        }
        if (requestDto.getPrice() != null) {
            this.price = requestDto.getPrice();
        }
        if (requestDto.getTicketTime() != null) {
            this.ticketTime = requestDto.getTicketTime();
        }
        if (requestDto.getDescription() != null) {
            this.description = requestDto.getDescription();
        }
    }
}


