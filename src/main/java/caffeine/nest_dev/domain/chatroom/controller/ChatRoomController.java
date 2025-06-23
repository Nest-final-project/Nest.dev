package caffeine.nest_dev.domain.chatroom.controller;

import caffeine.nest_dev.common.dto.CommonResponse;
import caffeine.nest_dev.common.dto.SliceResponse;
import caffeine.nest_dev.common.enums.SuccessCode;
import caffeine.nest_dev.domain.chatroom.dto.request.CreateChatRoomRequestDto;
import caffeine.nest_dev.domain.chatroom.dto.response.ChatRoomReadDto;
import caffeine.nest_dev.domain.chatroom.dto.response.ChatRoomResponseDto;
import caffeine.nest_dev.domain.chatroom.dto.response.MessageDto;
import caffeine.nest_dev.domain.chatroom.service.ChatRoomService;
import caffeine.nest_dev.domain.user.entity.UserDetailsImpl;
import java.net.URI;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommonResponse<ChatRoomResponseDto>> createChatRooms(
            @RequestBody CreateChatRoomRequestDto requestDto
    ) {
        log.info("chatRoom 생성 api 진입");
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
    @GetMapping
    public ResponseEntity<SliceResponse<ChatRoomReadDto>> findAllChatRooms(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cursorTime,
            @RequestParam(required = false) Long lastMessageId,
            @PageableDefault(size = 10) Pageable pageable
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
    @GetMapping("{chatRoomId}/messages")
    public ResponseEntity<SliceResponse<MessageDto>> findAllMessages(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long chatRoomId,
            @RequestParam(required = false) Long lastMessageId,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Long userId = userDetails.getId();
        Slice<MessageDto> messageList = chatRoomService.findAllMessage(userId, chatRoomId, lastMessageId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(SliceResponse.of(messageList));
    }
}
