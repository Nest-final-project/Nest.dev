package caffeine.nest_dev.domain.chatroom.controller;

import caffeine.nest_dev.common.dto.CommonResponse;
import caffeine.nest_dev.common.enums.SuccessCode;
import caffeine.nest_dev.domain.chatroom.dto.request.CreateChatRoomRequestDto;
import caffeine.nest_dev.domain.chatroom.dto.response.ChatRoomResponseDto;
import caffeine.nest_dev.domain.chatroom.service.ChatRoomService;
import caffeine.nest_dev.domain.user.entity.UserDetailsImpl;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
            @RequestBody CreateChatRoomRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        log.info("chatRoom 생성 api 진입");
        Long userId = userDetails.getId();
        ChatRoomResponseDto responseDto = chatRoomService.createChatRooms(requestDto, userId);
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
        List<ChatRoomResponseDto> dtoList = chatRoomService.findAllChatRooms(userDetails.getUser());
        return ResponseEntity.ok().body(CommonResponse.of(SuccessCode.SUCCESS_CHATROOM_READ, dtoList));
    }
}
