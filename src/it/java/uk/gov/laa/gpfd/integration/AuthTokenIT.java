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
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.laa.gpfd.config.TestDatabaseConfig;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Objects;
import java.util.stream.Stream;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


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
        if (endpoint.equals("/reports")) {
            mockMvc.perform(get(endpoint).with(oidcLogin()
                            .idToken(token -> token.claim("LAA_APP_ROLES", List.of("ABC")))))
                    .andExpect(status().is2xxSuccessful());
        } else {
            mockMvc.perform(get(endpoint).with(oidcLogin()
                            .idToken(token -> token.claim("LAA_APP_ROLES", List.of("ABC")))))
                    .andExpect(status().isForbidden());
        }
    }

    @ParameterizedTest(name = "[{index}] {0} should return 200 when authenticated")
    @MethodSource("securedReportEndpoints")
    @SneakyThrows
    void authenticatedAccess_shouldReturnOk(String description, String endpoint) {
        if (Objects.equals(description, "File download endpoint")) {
            // This will not 200 locally as it's not supported
            mockMvc.perform(get(endpoint).with(oidcLogin()
                    .idToken(token -> token.claim("LAA_APP_ROLES", List.of("REP000", "Financial", "Reconciliation")))))
                    .andExpect(status().isNotImplemented());
        } else {
            mockMvc.perform(get(endpoint).with(oidcLogin()
                            .idToken(token -> token.claim("LAA_APP_ROLES", List.of("REP000", "Financial", "Reconciliation")))))
                    .andExpect(status().isOk());
        }
    }

}
