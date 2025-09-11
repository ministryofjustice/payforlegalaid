package uk.gov.laa.gpfd.config;

import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.ZoneId;

@Component
public class SystemTimeProvider implements TimeProvider {
    @Override
    public LocalTime getCurrentTime() {
        return LocalTime.now(ZoneId.of("Europe/London"));
    }
}