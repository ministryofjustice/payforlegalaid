package uk.gov.laa.gpfd.integration.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import uk.gov.laa.gpfd.config.TimeProvider;

import java.time.LocalTime;

/**
 * Overrides the clock for out-of-hours integration tests only.
 */
@TestConfiguration
@Profile("!testat")
public class TestTimeConfig {
    @Bean
    @Primary
    public TimeProvider timeProvider() {
        return () -> LocalTime.of(6, 30);
    }
}
