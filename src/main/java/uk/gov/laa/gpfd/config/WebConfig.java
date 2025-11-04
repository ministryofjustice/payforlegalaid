package uk.gov.laa.gpfd.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final TimeBasedAccessInterceptor timeInterceptor;
    private final RequestResponseInterceptor requestResponseInterceptor;

    @Autowired
    public WebConfig(TimeBasedAccessInterceptor timeInterceptor, RequestResponseInterceptor requestResponseInterceptor) {
        this.timeInterceptor = timeInterceptor;
        this.requestResponseInterceptor = requestResponseInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(timeInterceptor);
        registry.addInterceptor(requestResponseInterceptor).addPathPatterns("/reports/**", "/excel/**", "/csv/**");
    }
}
