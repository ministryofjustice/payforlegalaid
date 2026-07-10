package uk.gov.laa.gpfd.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private static final List<String> EXCLUDED_PATHS = List.of(
            "/error",
            "/govuk/**",
            "/moj/**",
            "/css/**",
            "/js/**",
            "/images/**",
            "/webjars/**",
            "/assets/**",
            "/favicon.ico",
            "/favicon.svg"
    );
    private final TimeBasedAccessInterceptor timeInterceptor;
    private final RequestResponseInterceptor requestResponseInterceptor;

    @Autowired
    public WebConfig(TimeBasedAccessInterceptor timeInterceptor, RequestResponseInterceptor requestResponseInterceptor) {
        this.timeInterceptor = timeInterceptor;
        this.requestResponseInterceptor = requestResponseInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(timeInterceptor)
                .excludePathPatterns(EXCLUDED_PATHS);
        registry.addInterceptor(requestResponseInterceptor)
                .excludePathPatterns(EXCLUDED_PATHS);
    }
}
