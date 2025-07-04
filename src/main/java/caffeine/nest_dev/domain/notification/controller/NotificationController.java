package caffeine.nest_dev.domain.notification.controller;

import caffeine.nest_dev.common.dto.CommonResponse;
import caffeine.nest_dev.common.dto.PagingResponse;
import caffeine.nest_dev.common.enums.SuccessCode;
import caffeine.nest_dev.domain.notification.dto.response.NotificationResponseDto;
import caffeine.nest_dev.domain.notification.service.NotificationService;
import caffeine.nest_dev.domain.user.entity.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * SSE 를 통해 실시간 알림 구독 기능을 제공합니다.
 *
 * <p>
 * 클라이언트는 GET /sse/notifications/subscribe 요청을 보내 실시간 알림 스트림을 구독합니다. 인증된 사용자만 구독 가능하며, SSE 연결 시 마지막으로 수신한 이벤트 ID를
 * Last-Event-Id 헤더로 전달하면, 해당 ID 이후의 알림부터 전송합니다.
 * </p>
 */
@Tag(name = "Notification", description = "실시간 알림 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/sse")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * SSE 연결 생성 클라이언트는 Authorization 헤더를 통해 인증 토큰을 제공해야 함
     *
     * @param userDetails 인증된 사용자 정보
     * @param lastEventId 클라이언트가 마지막으로 수신한 이벤트 id
     * @return SseEmitter 실시간 알림 스트림 객체
     */
    @Operation(summary = "SSE 알림 구독", description = "실시간 알림을 받기 위해 SSE 연결을 생성합니다")
    @ApiResponse(responseCode = "200", description = "SSE 연결 생성 성공")
    @GetMapping(value = "/notifications/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(
            @Parameter(description = "인증된 사용자 정보") @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Parameter(description = "클라이언트가 마지막으로 수신한 이벤트 ID") @RequestHeader(value = "Last-Event-Id", required = false, defaultValue = "") String lastEventId) {
        Long userId = userDetails.getId();
        return notificationService.subscribe(userId, lastEventId);
    }

    // 알림 내역 조회
    @Operation(summary = "알림 내역 조회", description = "사용자의 알림 내역을 페이징하여 조회합니다")
    @ApiResponse(responseCode = "200", description = "알림 내역 조회 성공")
    @GetMapping("/notifications")
    public ResponseEntity<CommonResponse<PagingResponse<NotificationResponseDto>>> getNotifications(
            @Parameter(description = "인증된 사용자 정보") @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Parameter(description = "페이지 정보") @PageableDefault() Pageable pageable
    ) {
        PagingResponse<NotificationResponseDto> dtoList = notificationService.getNotifications(userDetails.getId(),
                pageable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_NOTIFICATION_READ,
                        dtoList));
    }
}
