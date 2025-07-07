package caffeine.nest_dev.common.websocket.redis;

import caffeine.nest_dev.domain.message.dto.response.MessageResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

/*
 * Redis 발행 서비스
 * <p>
 * 이 서비스를 통해 Redis Pub/Sub 채널에 메시지를 발행하면, 다른 서버에서 구독중인 Redis 구독 서비스(RedisSubscriber)가 메시지를 수신해 처리합니다.
 * </P>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisPublisher {

    private final RedisTemplate<String, Object> chatRedisTemplate;

    /**
     * Redis Pub/Sub 채널에 메시지 발행
     * <p>
     * 발행된 메시지는 구독자에게 전달되어 WebSocket으로 실시간 전파됩니다.
     * </p>
     *
     * @param topic      발행할 Redis 채널(chatRoom)
     * @param messageDto 발행할 메시지 DTO (MessageResponseDto)
     */
    public void publish(ChannelTopic topic, MessageResponseDto messageDto) {
        log.info("Redis 메시지 발행 시작 - 채널: {}, 발신자: {}, 수신자: {}, 내용: {}",
                topic.getTopic(),
                messageDto.getSenderId(),
                messageDto.getReceiverId(),
                messageDto.getContent());

        chatRedisTemplate.convertAndSend(topic.getTopic(), messageDto);

        log.info("Redis 메시지 발행 완료 - 채널: {}", topic.getTopic());
    }
}
