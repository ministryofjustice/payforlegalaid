package uk.gov.laa.gpfd.controller.ui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
public class CspReportController {

    private final ObjectMapper objectMapper;
    private final Counter cspViolations;

    public CspReportController(MeterRegistry registry, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;

        this.cspViolations = Counter.builder("csp_violations_total")
                .description("Total number of CSP violation reports received")
                .register(registry);
    }

    @PostMapping(value = "/csp-report", consumes = "application/csp-report")
    public ResponseEntity<Void> report(@RequestBody String rawBody) {

        try {
            Map<String, Object> payload =
                    objectMapper.readValue(rawBody, new TypeReference<>() {});

            Map<String, Object> report =
                    objectMapper.convertValue(payload.get("csp-report"), new TypeReference<>() {});

            String directive = String.valueOf(
                    report.getOrDefault("violated-directive", "unknown")
            );

            String blockedUri = String.valueOf(
                    report.getOrDefault("blocked-uri", "unknown")
            );

            log.info("CSP Violation detected: directive={}, blockedUri={}",
                    directive, blockedUri);

        } catch (Exception _) {
            log.info("CSP Violation (unparseable payload)");
        }

        cspViolations.increment();

        return ResponseEntity.noContent().build();
    }
}