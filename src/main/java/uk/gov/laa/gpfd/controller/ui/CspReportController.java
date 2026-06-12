package uk.gov.laa.gpfd.controller.ui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@Slf4j
public class CspReportController {

    private static final int MAX_URI_LENGTH = 100;
    private static final String UNKNOWN = "unknown";

    private final ObjectMapper objectMapper;
    private final MeterRegistry meterRegistry;

    public CspReportController(MeterRegistry registry, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.meterRegistry = registry;
    }

    @PostMapping(value = "/csp-report", consumes = "application/csp-report")
    public ResponseEntity<Void> report(@RequestBody(required = false) String rawBody) {
        if (rawBody != null && !rawBody.isBlank()) {
            try {
                Map<String, Object> payload =
                        objectMapper.readValue(rawBody, new TypeReference<>() {});
                Map<String, Object> report =
                        objectMapper.convertValue(payload.get("csp-report"), new TypeReference<>() {});

                if (report != null) {
                    String directive = String.valueOf(
                            report.getOrDefault("violated-directive", UNKNOWN)
                    );
                    String blockedUri = sanitise(String.valueOf(
                            report.getOrDefault("blocked-uri", UNKNOWN)
                    ));

                    log.info("CSP Violation detected: directive={}, blockedUri={}",
                            directive, blockedUri);
                } else {
                    log.info("CSP Violation (no csp-report field in payload)");
                }
            } catch (IOException _) {
                log.info("CSP Violation (unparseable payload)");
            }
        } else {
            log.info("CSP Violation (empty body)");
        }

        meterRegistry.counter("csp_violations_total").increment();

        return ResponseEntity.noContent().build();
    }

    private String sanitise(String value) {
        if (value == null) return UNKNOWN;
        String stripped = value.replaceAll("[\\r\\n\\t]", " ").trim();
        return stripped.length() > MAX_URI_LENGTH
                ? stripped.substring(0, MAX_URI_LENGTH) + "…"
                : stripped;
    }
}