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

    private final Counter cspViolations;

    public CspReportController(MeterRegistry registry) {
        this.cspViolations = Counter.builder("csp_violations_total")
                .description("Total number of CSP violations")
                .register(registry);
    }

    @PostMapping(value = "/csp-report", consumes = "application/csp-report")
    public ResponseEntity<Void> report(@RequestBody String rawBody, ObjectMapper objectMapper) {
        try {
            Map<String, Object> payload = objectMapper.readValue(rawBody, new TypeReference<>() {});
            Object violation = payload.get("csp-report");
            log.warn("CSP Violation: {}", violation != null ? violation : payload);
        } catch (Exception e) {
            log.warn("CSP Violation (unparseable): {}", rawBody);
        }
        cspViolations.increment();
        return ResponseEntity.noContent().build();
    }
}