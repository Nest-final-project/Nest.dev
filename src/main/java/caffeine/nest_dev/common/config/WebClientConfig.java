package caffeine.nest_dev.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration // Spring 설정 클래스 명시
public class WebClientConfig {

    @Bean // WebClient Spring Bean  등록
    public WebClient webClient() {
        return WebClient.builder().filter(logRequest())       // 요청 로깅 필터
                .filter(logResponse())      // 응답 로깅 필터
                .build();                   // 커스텀 필터 적용된 WebClient 인스턴스 생성 후 반환
    }

    // 요청을 로깅하는 ExchangeFilterFunction
    private ExchangeFilterFunction logRequest() {
        return (clientRequest, next) -> {
            log.info("[WebClient] Request URI: {}", clientRequest.url());
            log.info("[WebClient] Request Method: {}", clientRequest.method());
            log.info("[WebClient] Request Headers: {}", clientRequest.headers());

            // 로그 -> 다음 필터 OR 실제 요청으로 넘김
            return next.exchange(clientRequest);
        };
    }

    // 응답을 로깅하는 ExchangeFilterFunction
    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            log.info("[WebClient] Response Status: {}", clientResponse.statusCode());
            // 응답 헤더 로깅
            clientResponse.headers().asHttpHeaders().forEach((name, values) -> values.forEach(
                    value -> log.info("[WebClient] Response Header: {}={}", name, value)));
            // 로그 -> 응답 다음으로 넘김
            return Mono.just(clientResponse);
        });
    }
}