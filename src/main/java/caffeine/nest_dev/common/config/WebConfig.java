package caffeine.nest_dev.common.config;

import caffeine.nest_dev.oauth2.controller.converter.OAuth2ProviderConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // PathVariable 을 enum으로 받을 수 있도록 converter 등록
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new OAuth2ProviderConverter());
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 경로에 대해 CORS 허용
                .allowedOrigins("https://www.nest-dev.click", "http://localhost:3000",
                        "http://localhost:3001") // 프론트엔드 애플리케이션의 URL 명시
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS") // 허용할 HTTP 메소드
                .allowedHeaders("*") // 모든 헤더 허용
                .allowCredentials(true) // 자격 증명(쿠키, HTTP 인증 등) 허용
                .maxAge(3600); // Pre-flight 요청 결과를 캐시할 시간 (초 단위)

        registry.addMapping("/sse/**") // SSE 엔드포인트만 제한적으로 허용
                .allowedOrigins("https://www.nest-dev.click", "http://localhost:3000", "http://localhost:3001")
                .allowedMethods("GET")
                .allowedHeaders("Authorization", "Content-Type", "Last-Event-Id")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
