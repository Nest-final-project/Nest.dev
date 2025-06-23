package caffeine.nest_dev.domain.ticket.controller;

import caffeine.nest_dev.common.dto.CommonResponse;
import caffeine.nest_dev.common.enums.SuccessCode;
import caffeine.nest_dev.domain.ticket.dto.request.TicketRequestDto;
import caffeine.nest_dev.domain.ticket.dto.response.TicketResponseDto;
import caffeine.nest_dev.domain.ticket.service.TicketService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TicketController {

    private final TicketService ticketService;

    @PostMapping("/admin/ticket")
    public ResponseEntity<CommonResponse<TicketResponseDto>> registerTicket(
            @RequestBody TicketRequestDto requestDto) {
        TicketResponseDto responseDto = ticketService.saveTicket(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.of(SuccessCode.SUCCESS_TICKET_CREATED, responseDto));
    }

    @GetMapping("/ticket")
    public ResponseEntity<CommonResponse<List<TicketResponseDto>>> findTicketList() {
        List<TicketResponseDto> responseDtos = ticketService.getTicket();
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_TICKET_READ, responseDtos));
    }

    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<CommonResponse<TicketResponseDto>> findTicket(
            @PathVariable Long ticketId) {
        TicketResponseDto responseDto = ticketService.getTicketById(ticketId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_TICKET_READ, responseDto));
    }

    @PatchMapping("/admin/ticket/{ticketId}")
    public ResponseEntity<CommonResponse<Void>> updateTicket(
            @PathVariable Long ticketId, @RequestBody TicketRequestDto requestDto) {
        ticketService.modifyTicket(ticketId, requestDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_TICKET_UPDATED));
    }

    @DeleteMapping("/admin/ticket/{ticketId}")
    public ResponseEntity<CommonResponse<Void>> deleteTicket(
            @PathVariable Long ticketId) {
        ticketService.removeTicket(ticketId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_TICKET_DELETED));
    }
}
