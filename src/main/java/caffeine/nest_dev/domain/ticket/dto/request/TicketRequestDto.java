package caffeine.nest_dev.domain.ticket.dto.request;

import caffeine.nest_dev.domain.ticket.entity.Ticket;
import caffeine.nest_dev.domain.ticket.enums.TicketTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TicketRequestDto {

    private String name;
    private Integer price;
    private String description;
    private TicketTime ticketTime;

    public Ticket toEntity() {
        return Ticket.builder()
                .name(name)
                .price(price)
                .ticketTime(ticketTime)
                .description(description)
                .build();
    }
}
