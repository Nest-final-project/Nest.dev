package caffeine.nest_dev.domain.ticket.service;

import caffeine.nest_dev.domain.ticket.dto.request.TicketRequestDto;
import caffeine.nest_dev.domain.ticket.dto.response.TicketResponseDto;
import caffeine.nest_dev.domain.ticket.entity.Ticket;
import caffeine.nest_dev.domain.ticket.repository.TicketRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;

    @Transactional
    public TicketResponseDto saveTicket(TicketRequestDto requestDto) {
        Ticket ticket = ticketRepository.save(requestDto.toEntity());
        return TicketResponseDto.of(ticket);
    }

    @Transactional(readOnly = true)
    public List<TicketResponseDto> getTeicket() {
        List<TicketResponseDto> list = ticketRepository.findAll().stream()
                .map(TicketResponseDto::of).toList();
        return list;
    }
}
