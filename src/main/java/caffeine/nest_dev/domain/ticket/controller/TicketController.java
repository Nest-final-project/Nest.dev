package caffeine.nest_dev.domain.ticket.controller;

import caffeine.nest_dev.common.dto.CommonResponse;
import caffeine.nest_dev.common.enums.SuccessCode;
import caffeine.nest_dev.domain.ticket.dto.request.TicketRequestDto;
import caffeine.nest_dev.domain.ticket.dto.response.TicketResponseDto;
import caffeine.nest_dev.domain.ticket.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Ticket", description = "티켓 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TicketController {

    private final TicketService ticketService;

    @Operation(summary = "티켓 등록", description = "관리자가 새로운 티켓을 등록합니다")
    @ApiResponse(responseCode = "201", description = "티켓 등록 성공")
    @PostMapping("/admin/ticket")
    public ResponseEntity<CommonResponse<TicketResponseDto>> registerTicket(
            @Parameter(description = "티켓 등록 요청 정보") @RequestBody TicketRequestDto requestDto) {
        TicketResponseDto responseDto = ticketService.saveTicket(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.of(SuccessCode.SUCCESS_TICKET_CREATED, responseDto));
    }

    @Operation(summary = "티켓 목록 조회", description = "등록된 티켓 목록을 조회합니다")
    @ApiResponse(responseCode = "200", description = "티켓 목록 조회 성공")
    @GetMapping("/ticket")
    public ResponseEntity<CommonResponse<List<TicketResponseDto>>> findTicketList() {
        List<TicketResponseDto> responseDtos = ticketService.getTicket();
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_TICKET_READ, responseDtos));
    }

    @Operation(summary = "티켓 상세 조회", description = "특정 티켓의 상세 정보를 조회합니다")
    @ApiResponse(responseCode = "200", description = "티켓 상세 조회 성공")
    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<CommonResponse<TicketResponseDto>> findTicket(
            @Parameter(description = "조회할 티켓 ID") @PathVariable Long ticketId) {
        TicketResponseDto responseDto = ticketService.getTicketById(ticketId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_TICKET_READ, responseDto));
    }

    @Operation(summary = "티켓 수정", description = "관리자가 기존 티켓 정보를 수정합니다")
    @ApiResponse(responseCode = "200", description = "티켓 수정 성공")
    @PatchMapping("/admin/ticket/{ticketId}")
    public ResponseEntity<CommonResponse<Void>> updateTicket(
            @Parameter(description = "수정할 티켓 ID") @PathVariable Long ticketId, 
            @Parameter(description = "티켓 수정 요청 정보") @RequestBody TicketRequestDto requestDto) {
        ticketService.modifyTicket(ticketId, requestDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_TICKET_UPDATED));
    }

    @Operation(summary = "티켓 삭제", description = "관리자가 티켓을 삭제합니다")
    @ApiResponse(responseCode = "200", description = "티켓 삭제 성공")
    @DeleteMapping("/admin/ticket/{ticketId}")
    public ResponseEntity<CommonResponse<Void>> deleteTicket(
            @Parameter(description = "삭제할 티켓 ID") @PathVariable Long ticketId) {
        ticketService.removeTicket(ticketId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_TICKET_DELETED));
    }
}
