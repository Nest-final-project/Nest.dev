package caffeine.nest_dev.common.websocket.config;

import caffeine.nest_dev.domain.message.dto.response.MessageResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

/**
 * Redis 구독 서비스
 * <p>
 * Redis Pub/Sub 을 통해 발행된 메시지를 수신해 STOMP WebSocket 구독자에게 실시간으로 메시지를 전달합니다.
 * </p>
 *
 * <pre>
 *     동작 흐름:
 *     1) Redis에서 메시지 수신
 *     2) JSON 문자열 -> ChatMessageDto 변환
 *     3) STOMP 구독 경로 (/sub/chat_room/{chatRoomId}에 메시지 브로드캐스트
 * </pre>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> chatRedisTemplate;

    // STOMP 메시지를 클라이언트로 보내기 위한 인터페이스
    private final SimpMessageSendingOperations messagingTemplate;

    /**
     * Redis Pub/Sub 메시지를 수실할 때 호출
     * <p>
     * 메시지를 ChatMessageDto로 변환해 STOMP 구독 경로로 전달
     * </p>
     *
     * @param message Redis에서 수신한 메시지
     * @param pattern 구독 패턴
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String channelName = new String(message.getChannel());
            String pubMessage = chatRedisTemplate.getStringSerializer().deserialize(message.getBody());

            log.info("🔔 Redis 메시지 수신 - 채널: {}, 내용: {}", channelName, pubMessage);

            // JSON -> DTO
            MessageResponseDto roomMessage = objectMapper.readValue(pubMessage, MessageResponseDto.class);

            log.info("📤 STOMP로 전달 - 수신자: {}, 채팅방: {}, 메시지: {}",
                    roomMessage.getReceiverId(),
                    roomMessage.getChatRoomId(),
                    roomMessage.getContent());

            // STOMP 구독자에게 메시지 전달
            messagingTemplate.convertAndSendToUser(
                    String.valueOf(roomMessage.getReceiverId()),
                    "/queue/message",
                    roomMessage
            );

            log.info("STOMP 메시지 전달 완료 - 수신자: {}", roomMessage.getReceiverId());
        } catch (Exception e) {
            log.error("Redis 메시지 처리 실패", e);
        }
    }
}
