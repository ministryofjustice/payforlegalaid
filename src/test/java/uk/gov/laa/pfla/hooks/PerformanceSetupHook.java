package uk.gov.laa.pfla.hooks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.Before;
import org.junit.platform.commons.logging.Logger;
import uk.gov.laa.gpfd.model.ReportsGet200Response;
import uk.gov.laa.pfla.performance.PerformanceReportRegistry;
import uk.gov.laa.pfla.service.HttpProvider;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.platform.commons.logging.LoggerFactory.getLogger;
import static uk.gov.laa.pfla.scenario.AuthenticationState.AUTHENTICATED;

public class PerformanceSetupHook {

    private static final Logger logger = getLogger(SkipScenarioPredicate.class);
    private final HttpProvider httpProvider;
    private final ObjectMapper objectMapper;

    private static final AtomicBoolean initialised = new AtomicBoolean(false);

    public PerformanceSetupHook(HttpProvider httpProvider, ObjectMapper objectMapper) {
        this.httpProvider = httpProvider;
        this.objectMapper = objectMapper;
    }

    @Before("@performance")
    public void fetchReports() {
        // Prevent duplicate initialisation (safe for parallel runs)
        if (!initialised.compareAndSet(false, true)) {
            return;
        }

        // Performance tests use Basic Auth
        httpProvider.setAuthenticationState(AUTHENTICATED);

        var response = httpProvider.getClient()
                .getForEntity("/reports", String.class);

        // Check HTTP status first
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException(
                    "Failed to fetch /reports. Status: " + response.getStatusCode()
            );
        }

        String responseBody = response.getBody();

        // Check body exists
        if (responseBody == null || responseBody.isBlank()) {
            throw new IllegalStateException(
                    "Failed to fetch /reports — empty response body"
            );
        }

        ReportsGet200Response reportsResponse;
        try {
            reportsResponse = objectMapper.readValue(
                    responseBody,
                    ReportsGet200Response.class
            );
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(
                    "Failed to parse /reports response",
                    e
            );
        }

        // Validate report list exists
        if (reportsResponse.getReportList() == null) {
            throw new IllegalStateException(
                    "/reports returned null report list"
            );
        }

        PerformanceReportRegistry.validateReportsExist(
                reportsResponse.getReportList()
        );

        logger.info(() -> "Performance benchmark reports validated against API response");
    }
}