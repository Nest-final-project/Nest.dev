package caffeine.nest_dev.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient restClient() {

        // 네트워크 안정성을 위해 시간 설정
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000); // 연결 타임아웃 5초
        factory.setReadTimeout(10000); // 읽기 타임아웃 10초

        return RestClient.builder()
                .requestFactory(factory)
                .build();
    }

}
