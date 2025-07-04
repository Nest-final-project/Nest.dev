package caffeine.nest_dev.domain.chatroom.controller;

import caffeine.nest_dev.common.dto.CommonResponse;
import caffeine.nest_dev.common.dto.SliceResponse;
import caffeine.nest_dev.common.enums.SuccessCode;
import caffeine.nest_dev.domain.chatroom.dto.request.CreateChatRoomRequestDto;
import caffeine.nest_dev.domain.chatroom.dto.response.ChatRoomReadDto;
import caffeine.nest_dev.domain.chatroom.dto.response.ChatRoomResponseDto;
import caffeine.nest_dev.domain.chatroom.dto.response.ChatRoomStatusResponseDto;
import caffeine.nest_dev.domain.chatroom.dto.response.MessageDto;
import caffeine.nest_dev.domain.chatroom.service.ChatRoomService;
import caffeine.nest_dev.domain.user.entity.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "ChatRoom", description = "채팅방 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat_rooms")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    /**
     * 채팅방 생성 (테스트/ 장애 복구용)
     *
     * @param requestDto 예약 Id
     * @return
     */
    @Operation(summary = "채팅방 생성", description = "관리자가 테스트 또는 장애 복구를 위해 채팅방을 생성합니다")
    @ApiResponse(responseCode = "201", description = "채팅방 생성 성공")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommonResponse<ChatRoomResponseDto>> createChatRooms(
            @Parameter(description = "채팅방 생성 요청 정보") @RequestBody CreateChatRoomRequestDto requestDto
    ) {
        ChatRoomResponseDto responseDto = chatRoomService.createChatRooms(requestDto);
        return ResponseEntity.created(URI.create("/api/chat_rooms"))
                .body(CommonResponse.of(SuccessCode.SUCCESS_CHATROOM_CREATED, responseDto));
    }

    /**
     * 채팅방 목록 조회
     *
     * @param userDetails 사용자 인증 객체
     * @return 채팅방 목록
     */
    @Operation(summary = "채팅방 목록 조회", description = "사용자의 채팅방 목록을 조회합니다")
    @ApiResponse(responseCode = "200", description = "채팅방 목록 조회 성공")
    @GetMapping
    public ResponseEntity<SliceResponse<ChatRoomReadDto>> findAllChatRooms(
            @Parameter(description = "인증된 사용자 정보") @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Parameter(description = "커서 시간") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cursorTime,
            @Parameter(description = "마지막 메시지 ID") @RequestParam(required = false) Long lastMessageId,
            @Parameter(description = "페이지 정보") @PageableDefault(size = 10) Pageable pageable
    ) {
        Long userId = userDetails.getId();
        Slice<ChatRoomReadDto> dtoList = chatRoomService.findAllChatRooms(userId, lastMessageId, cursorTime,
                pageable);
        return ResponseEntity.status(HttpStatus.OK).body(SliceResponse.of(dtoList));
    }

    /**
     * 채팅 내역 불러오기
     *
     * @param userDetails   사용자 인증 객체
     * @param chatRoomId    채팅방 Id
     * @param lastMessageId 마지막 메시지 Id
     * @param pageable      페이지객체
     * @return 채팅 내역
     */
    @Operation(summary = "채팅 메시지 조회", description = "특정 채팅방의 메시지 내역을 조회합니다")
    @ApiResponse(responseCode = "200", description = "채팅 메시지 조회 성공")
    @GetMapping("{chatRoomId}/messages")
    public ResponseEntity<SliceResponse<MessageDto>> findAllMessages(
            @Parameter(description = "인증된 사용자 정보") @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Parameter(description = "채팅방 ID") @PathVariable Long chatRoomId,
            @Parameter(description = "마지막 메시지 ID") @RequestParam(required = false) Long lastMessageId,
            @Parameter(description = "페이지 정보") @PageableDefault(size = 10) Pageable pageable
    ) {
        Long userId = userDetails.getId();
        Slice<MessageDto> messageList = chatRoomService.findAllMessage(userId, chatRoomId, lastMessageId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(SliceResponse.of(messageList));
    }

    @Operation(summary = "채팅방 상태 확인", description = "채팅방이 닫혔는지 확인합니다")
    @ApiResponse(responseCode = "200", description = "채팅방 상태 조회 성공")
    @GetMapping("/{chatRoomId}/status")
    public ResponseEntity<ChatRoomStatusResponseDto> isClosed(
            @Parameter(description = "인증된 사용자 정보") @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Parameter(description = "채팅방 ID") @PathVariable Long chatRoomId
    ) {
        Long userId = userDetails.getId();
        ChatRoomStatusResponseDto responseDto = chatRoomService.isClosed(userId, chatRoomId);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
