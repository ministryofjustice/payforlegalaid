package uk.gov.laa.gpfd.controller.ui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@Slf4j
public class CspReportController {

    private final ObjectMapper objectMapper;
    private final MeterRegistry registry;

    // Cache counters per directive to avoid recreating them per request
    private final ConcurrentHashMap<String, Counter> counters = new ConcurrentHashMap<>();

    public CspReportController(MeterRegistry registry, ObjectMapper objectMapper) {
        this.registry = registry;
        this.objectMapper = objectMapper;
    }

    /**
     * Get or create a counter per CSP directive
     */
    private Counter counterFor(String directive) {
        return counters.computeIfAbsent(directive, d ->
                Counter.builder("csp_violations_total")
                        .description("Total number of CSP violation reports received")
                        .tag("directive", d)
                        .register(registry)
        );
    }

    @PostMapping(value = "/csp-report", consumes = "application/csp-report")
    public ResponseEntity<Void> report(@RequestBody String rawBody) {

        try {
            Map<String, Object> payload =
                    objectMapper.readValue(rawBody, new TypeReference<>() {});

            Object reportObj = payload.get("csp-report");

            Map<String, Object> report = objectMapper.convertValue(
                    reportObj,
                    new TypeReference<>() {}
            );

            String directive = String.valueOf(
                    report.getOrDefault("violated-directive", "unknown")
            );

            String blockedUri = String.valueOf(
                    report.getOrDefault("blocked-uri", "unknown")
            );

            log.info("CSP Violation detected: directive={}, blockedUri={}",
                    directive, blockedUri);

            // Increment metric per directive
            counterFor(directive).increment();

        } catch (Exception _) {
            log.info("CSP Violation (unparseable payload): {}", rawBody);
            counterFor("unparseable").increment();
        }

        return ResponseEntity.noContent().build();
    }
}