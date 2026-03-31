package uk.gov.laa.gpfd.integration;

import lombok.SneakyThrows;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import uk.gov.laa.gpfd.config.TestDatabaseConfig;
import uk.gov.laa.gpfd.integration.config.OAuth2TestConfig;
import uk.gov.laa.gpfd.integration.data.ReportTestData;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.laa.gpfd.integration.data.ReportTestData.ReportType.CSV_REPORT;

@SpringBootTest(
        webEnvironment = RANDOM_PORT,
        classes = {TestDatabaseConfig.class, OAuth2TestConfig.class}
)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application-test.yml")
final class GetReportsByIdIT extends BaseIT {

    @SneakyThrows
    @ParameterizedTest(name = "Given {0} when requested then returns correct URL")
    @MethodSource("uk.gov.laa.gpfd.integration.data.ReportTestData#getAllTestReports")
    void shouldReturnOkGivenValidExistedReport(ReportTestData testData) {
        var uri = "/reports/%s".formatted(testData.id());

        performGetRequest(uri)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testData.id()))
                .andExpect(jsonPath("$.reportName").value(testData.name()))
                .andExpect(jsonPath("$.reportDownloadUrl").value(testData.expectedUrl()));
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(strings = {"excel", "csv"})
    void shouldReturn400WhenGivenInvalidId(String type) {
        var uri = "/%s/%s321".formatted(type, CSV_REPORT.getReportData().id());

        performGetRequest(uri)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.error")
                        .value("Error: Invalid input for parameter id. Expected a numeric value"));
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(strings = {"excel", "csv"})
    void shouldReturn404WhenNoReportsFound(String type) {
        var nonExistentReportId = "0d4da9ec-b0b3-4371-af10-321";
        var uri = "/%s/%s".formatted(type, nonExistentReportId);

        performGetRequest(uri)
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.error")
                        .value("Report not found for ID 0d4da9ec-b0b3-4371-af10-000000000321"));
    }

}


