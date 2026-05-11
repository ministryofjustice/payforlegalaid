package uk.gov.laa.gpfd.integration;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import uk.gov.laa.gpfd.integration.config.TestTimeConfig;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestTimeConfig.class)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
final class OutOfHoursIT extends BaseIT {

    @SneakyThrows
    @Test
    void getReportsShouldReturn500WhenOutOfHours() {
        var uri = "/reports/";
        performGetRequest(uri)
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error")
                        .value("The service is unavailable between the hours of 22:00 and 07:00, Mon - Sun"));
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(strings = {"reports", "excel", "csv"})
    void getByIdShouldReturn500WhenOutOfHours(String type) {
        var uri = "/" + type + "/321";

        performGetRequest(uri)
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error")
                        .value("The service is unavailable between the hours of 22:00 and 07:00, Mon - Sun"));
    }

    @SneakyThrows
    @Test
    void getReportFileShouldReturn500WhenOutOfHours() {
        var uri = "/reports/321";
        performGetRequest(uri)
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error")
                        .value("The service is unavailable between the hours of 22:00 and 07:00, Mon - Sun"));
    }
}