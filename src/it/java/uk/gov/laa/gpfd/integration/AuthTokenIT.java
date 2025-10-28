package uk.gov.laa.gpfd.integration;

import static org.junit.jupiter.params.provider.Arguments.of;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.laa.gpfd.integration.data.ReportTestData.ReportType.CCMS_REPORT;
import static uk.gov.laa.gpfd.integration.data.ReportTestData.ReportType.REP012ID;
import static uk.gov.laa.gpfd.integration.data.ReportTestData.ReportType.CSV_REPORT;

import lombok.SneakyThrows;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import uk.gov.laa.gpfd.config.TestDatabaseConfig;

import java.util.Objects;
import java.util.stream.Stream;

@SpringBootTest(webEnvironment = RANDOM_PORT, classes = {TestDatabaseConfig.class})
@AutoConfigureMockMvc
@ActiveProfiles("testauth")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application-testauth.yml")
final class AuthTokenIT extends BaseIT {

    private static Stream<Arguments> securedReportEndpoints() {
        return Stream.of(
                of("Root api endpoint", "/reports"),
                of("Specific report endpoint", "/reports/%s".formatted(CSV_REPORT.getReportData().id())),
                of("Excel download endpoint", "/excel/%s".formatted(CCMS_REPORT.getReportData().id())),
                of("CSV download endpoint", "/csv/%s".formatted(CSV_REPORT.getReportData().id())),
                of("File download endpoint", "/reports/%s/file".formatted(REP012ID.getReportData().id()))
        );
    }

    @ParameterizedTest(name = "[{index}] {0} should redirect when unauthenticated")
    @MethodSource("securedReportEndpoints")
    @SneakyThrows
    void unauthenticatedAccess_shouldRedirectToLogin(String description, String endpoint) {
        performGetRequest(endpoint)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("http://localhost/oauth2/authorization/azure*"));
    }

    @ParameterizedTest(name = "[{index}] {0} should return 200 when authenticated")
    @MethodSource("securedReportEndpoints")
    @WithMockUser(username = "Mock User")
    @SneakyThrows
    void authenticatedAccess_shouldReturnOk(String description, String endpoint) {
        if (Objects.equals(description, "File download endpoint")) {
            // This will not 200 locally as it's not supported
            performGetRequest(endpoint).andExpect(status().isNotImplemented());
        } else {
            performGetRequest(endpoint).andExpect(status().isOk());
        }
    }

}
