package caffeine.nest_dev.domain.ticket.service;

import caffeine.nest_dev.common.enums.ErrorCode;
import caffeine.nest_dev.common.exception.BaseException;
import caffeine.nest_dev.domain.ticket.dto.request.TicketRequestDto;
import caffeine.nest_dev.domain.ticket.dto.response.TicketResponseDto;
import caffeine.nest_dev.domain.ticket.entity.Ticket;
import caffeine.nest_dev.domain.ticket.repository.TicketRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
    public List<TicketResponseDto> getTicket() {
        List<TicketResponseDto> list = ticketRepository.findAll().stream()
                .map(TicketResponseDto::of).toList();
        return list;
    }

    @Transactional
    public void modifyTicket(Long ticketId, TicketRequestDto requestDto) {
        Ticket ticket = findTicketById(ticketId);
        ticket.modifyTicket(requestDto);
    }

    @Transactional
    public void removeTicket(Long ticketId) {
        Ticket ticket = findTicketById(ticketId);
        ticketRepository.delete(ticket);
    }

    private Ticket findTicketById(Long ticketId) {
        return ticketRepository.findById(ticketId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_TICKET));
    }

}
