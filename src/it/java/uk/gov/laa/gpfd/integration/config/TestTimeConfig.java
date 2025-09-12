package config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import uk.gov.laa.gpfd.config.TimeProvider;

import java.time.LocalTime;

/**
 * Class overwrites time used when calling endpoints in test, allowing simulation of our of hours response
 */
@TestConfiguration
public class TestTimeConfig {
    @Bean
    public TimeProvider timeProvider() {
        // Simulate 6:30 AM (before working hours)
        return new FixedTimeProvider(LocalTime.of(6, 30));
    }

    public static class FixedTimeProvider implements TimeProvider {
        private final LocalTime fixedTime;

        public FixedTimeProvider(LocalTime fixedTime) {
            this.fixedTime = fixedTime;
        }

        @Override
        public LocalTime getCurrentTime() {
            return fixedTime;
        }
    }
}