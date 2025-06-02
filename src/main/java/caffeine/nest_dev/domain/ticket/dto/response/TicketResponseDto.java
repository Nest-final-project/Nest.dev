package caffeine.nest_dev.domain.ticket.dto.response;

import caffeine.nest_dev.domain.payment.dto.response.PaymentResponseDto;
import caffeine.nest_dev.domain.ticket.entity.Ticket;
import caffeine.nest_dev.domain.ticket.enums.TicketTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TicketResponseDto {

    private final Long id;
    private final String name;
    private final Integer price;
    private final String description;
    private final TicketTime ticketTime;
    private final List<PaymentResponseDto> payments;

    public static TicketResponseDto of(Ticket ticket) {
        return TicketResponseDto.builder()
                .id(ticket.getId())
                .name(ticket.getName())
                .price(ticket.getPrice())
                .description(ticket.getDescription())
                .ticketTime(ticket.getTicketTime())
                .payments(
                        ticket.getPayments().stream()
                                .map(PaymentResponseDto::of)
                                .collect(Collectors.toList()))
                .build();
    }
}
