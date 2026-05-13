package uk.gov.laa.gpfd.integration;

import lombok.SneakyThrows;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import uk.gov.laa.gpfd.integration.data.ReportTestData;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.laa.gpfd.integration.data.ReportTestData.ReportType.CSV_REPORT;

final class GetReportsByIdIT extends BaseIT {

    @SneakyThrows
    @ParameterizedTest(name = "Given {0} when requested then returns correct URL")
    @MethodSource("uk.gov.laa.gpfd.integration.data.ReportTestData#getAllTestReports")
    void shouldReturnOkGivenValidExistedReport(ReportTestData testData) {
        var uri = "/reports/%s".formatted(testData.id());

        performGetRequestWithRoles(uri, List.of("REP000", "Financial", "Reconciliation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testData.id()))
                .andExpect(jsonPath("$.reportName").value(testData.name()))
                .andExpect(jsonPath("$.reportDownloadUrl").value(testData.expectedUrl()));
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(strings = {"csv", "excel"})
    void shouldReturn400WhenGivenInvalidId(String type) {
        var uri = "/reports/%s321/%s".formatted(CSV_REPORT.getReportData().id(), type);

        performGetRequestWithRoles(uri, List.of("REP000", "Financial", "Reconciliation"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.error")
                        .value("Error: Invalid input for parameter id. Expected a numeric value"));
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(strings = {"csv", "excel"})
    void shouldReturn404WhenNoReportsFound(String type) {
        var nonExistentReportId = "0d4da9ec-b0b3-4371-af10-321";
        var uri = "/reports/%s/%s".formatted(nonExistentReportId, type);

        performGetRequestWithRoles(uri, List.of("REP000", "Financial", "Reconciliation"))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.error")
                        .value("You cannot access report with ID: 0d4da9ec-b0b3-4371-af10-000000000321"));
    }

}
