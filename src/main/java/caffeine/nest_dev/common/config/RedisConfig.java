package caffeine.nest_dev.common.config;

import caffeine.nest_dev.common.websocket.config.RedisSubscriber;
import caffeine.nest_dev.oauth2.dto.response.OAuth2LoginResponseDto;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;
    @Value("${spring.data.redis.port}")
    private int port;
    private static final String REDISSON_PREFIX = "redis://";

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress(REDISSON_PREFIX + host + ":" + port);
        return Redisson.create(config);
    }

    // 소셜 로그인 전용 redisTemplate
    @Bean
    public RedisTemplate<String, Object> objectRedisTemplate(
            RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(
                new Jackson2JsonRedisSerializer<>(OAuth2LoginResponseDto.class));

        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(
                new Jackson2JsonRedisSerializer<>(OAuth2LoginResponseDto.class));

        template.afterPropertiesSet();
        return template;
    }

    // 발행된 메시지 처리를 위한 리스너 설정
    @Bean
    RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            MessageListenerAdapter listenerAdapter,
            ChannelTopic channelTopic
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listenerAdapter, channelTopic);
        return container;
    }

    // RedisMessageListenerContainer 로부터 메시지를 dispatch 받고, 실제로 메시지를 처리하는 비지니스 로직이 담긴 subscriber를 추가함
    @Bean
    public MessageListenerAdapter listenerAdapter(RedisSubscriber subscriber) {
        return new MessageListenerAdapter(subscriber, "onMessage");
    }

    @Bean
    public RedisTemplate<String, Object> chatRedisTemplate(
            RedisConnectionFactory connectionFactory
    ) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(String.class));
        return template;
    }

    // topic 공유를 위해 channel Topic을 빈으로 등록해 단일화
    @Bean
    public ChannelTopic channelTopic() {
        return new ChannelTopic("chatroom");
    }

}
