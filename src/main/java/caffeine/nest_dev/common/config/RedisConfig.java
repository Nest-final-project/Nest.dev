package caffeine.nest_dev.common.config;

import caffeine.nest_dev.oauth2.dto.response.OAuth2LoginResponseDto;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
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

    @Bean
    public RedisTemplate<String, Object> chatRedisTemplate(
            RedisConnectionFactory redisConnectionFactory
    ) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

}
