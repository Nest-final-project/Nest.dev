package caffeine.nest_dev.common.config;

import caffeine.nest_dev.oauth2.controller.converter.OAuth2ProviderConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // PathVariable 을 enum으로 받을 수 있도록 converter 등록
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new OAuth2ProviderConverter());
    }
}
