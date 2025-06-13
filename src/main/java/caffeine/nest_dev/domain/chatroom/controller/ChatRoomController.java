package caffeine.nest_dev.domain.chatroom.controller;

import caffeine.nest_dev.common.dto.CommonResponse;
import caffeine.nest_dev.common.dto.SliceResponse;
import caffeine.nest_dev.common.enums.SuccessCode;
import caffeine.nest_dev.domain.chatroom.dto.request.CreateChatRoomRequestDto;
import caffeine.nest_dev.domain.chatroom.dto.response.ChatRoomResponseDto;
import caffeine.nest_dev.domain.chatroom.dto.response.MessageDto;
import caffeine.nest_dev.domain.chatroom.service.ChatRoomService;
import caffeine.nest_dev.domain.user.entity.UserDetailsImpl;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/chatrooms")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    /**
     * 채팅방 생성
     *
     * @param requestDto
     * @return
     */
    @PostMapping
    public ResponseEntity<CommonResponse<ChatRoomResponseDto>> createChatRooms(
            @RequestBody CreateChatRoomRequestDto requestDto
    ) {
        log.info("chatRoom 생성 api 진입");
        ChatRoomResponseDto responseDto = chatRoomService.createChatRooms(requestDto);
        return ResponseEntity.created(URI.create("/api/chatrooms"))
                .body(CommonResponse.of(SuccessCode.SUCCESS_CHATROOM_CREATED, responseDto));
    }

    // TODO : 무한 스크롤 구현

    /**
     * 채팅방 목록 조회
     *
     * @param userDetails 사용자 인증 객체
     * @return 채팅방 목록
     */
    @GetMapping
    public ResponseEntity<CommonResponse<List<ChatRoomResponseDto>>> findAllChatRooms(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        List<ChatRoomResponseDto> dtoList = chatRoomService.findAllChatRooms(userDetails.getId());
        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.of(SuccessCode.SUCCESS_CHATROOM_READ, dtoList));
    }

    /**
     * 채팅 내역 불러오기
     *
     * @param userDetails
     * @param chatRoomId
     * @param lastMessageId
     * @param pageable
     * @return
     */
    @GetMapping("/messages/{chatRoomId}")
    public ResponseEntity<SliceResponse<MessageDto>> findAllMessages(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long chatRoomId,
            @RequestParam(required = false) Long lastMessageId,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Long userId = userDetails.getId();
        Slice<MessageDto> messageList = chatRoomService.findAllMessage(chatRoomId, userId, lastMessageId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(SliceResponse.of(messageList));
    }
}
