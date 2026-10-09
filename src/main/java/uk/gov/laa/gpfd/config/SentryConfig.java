package uk.gov.laa.gpfd.config;

import io.sentry.SentryOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.laa.gpfd.utils.SentryEvents;

@Configuration(proxyBeanMethods = false)
public class SentryConfig {

    @Bean
    public SentryOptions.BeforeSendCallback sentryBeforeSend() {
        return (event, hint) -> {
            if (event.getThrowable() != null && !SentryEvents.shouldCaptureException(event.getThrowable())) {
                return null;
            }
            return event;
        };
    }
}