package uk.gov.laa.gpfd.integration;

import lombok.SneakyThrows;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.of;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.laa.gpfd.integration.data.ReportTestData.ReportType.CCMS_REPORT;
import static uk.gov.laa.gpfd.integration.data.ReportTestData.ReportType.CSV_REPORT;
import static uk.gov.laa.gpfd.integration.data.ReportTestData.ReportType.REP012ID;

final class AuthTokenIT extends BaseIT {

    private static Stream<Arguments> securedReportEndpoints() {
        return Stream.of(
                of("Root api endpoint", "/reports"),
                of("Specific report endpoint", "/reports/%s".formatted(CSV_REPORT.getReportData().id())),
                of("Excel download endpoint", "/reports/%s/excel".formatted(CCMS_REPORT.getReportData().id())),
                of("CSV download endpoint", "/reports/%s/csv".formatted(CSV_REPORT.getReportData().id())),
                of("File download endpoint", "/reports/%s/file".formatted(REP012ID.getReportData().id()))
        );
    }

    @ParameterizedTest(name = "[{index}] {0} should redirect when unauthenticated")
    @MethodSource("securedReportEndpoints")
    @SuppressWarnings("unused") // "description" is used by JUnit but CodeQL can't spot it
    void unauthenticatedAccess_shouldRedirectToLogin(String description, String endpoint) throws Exception {
        performGetRequest(endpoint)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/oauth2/authorization/gpfd-azure-dev"));
    }

    @ParameterizedTest
    @MethodSource("securedReportEndpoints")
    void authenticatedAccess_withNoValidRole(String description, String endpoint) throws Exception {
        if (Objects.equals(description, "Root api endpoint")) {
            // return 200 with empty list
            performGetRequestWithRoles(endpoint, List.of("ABC"))
                    .andExpect(status().is2xxSuccessful());
        } else {
            performGetRequestWithRoles(endpoint, List.of("ABC"))
                    .andExpect(status().isForbidden());
        }
    }

    @ParameterizedTest(name = "[{index}] {0} should return 200 when authenticated")
    @MethodSource("securedReportEndpoints")
    @SneakyThrows
    void authenticatedAccess_withValidRolesshouldReturnOk(String description, String endpoint) {
        if (Objects.equals(description, "File download endpoint")) {
            // This will not 200 locally as it's not supported
            performGetRequestWithRoles(endpoint, List.of("REP000", "Reconciliation"))
                    .andExpect(status().isNotImplemented());
        } else {
            performGetRequestWithRoles(endpoint, List.of("REP000", "Financial", "Reconciliation"))
                    .andExpect(status().isOk());
        }
    }

}
