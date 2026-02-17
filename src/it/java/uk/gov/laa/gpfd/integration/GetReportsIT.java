package uk.gov.laa.gpfd.integration;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import uk.gov.laa.gpfd.config.TestDatabaseConfig;
import uk.gov.laa.gpfd.integration.verifier.DatabaseVerifier;
import uk.gov.laa.gpfd.integration.verifier.DatabaseVerifier.Table;

@SpringBootTest(webEnvironment = RANDOM_PORT, classes = {TestDatabaseConfig.class})
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(PER_CLASS)
@TestPropertySource(locations = "classpath:application-test.yml")
final class GetReportsIT extends BaseIT {

    /*@Autowired
    private JdbcTemplate jdbc;

    @Test
    @SneakyThrows
    void shouldSuccessfullyReturnAllAvailableReports() {
        var reportsLen = DatabaseVerifier.rowCountFor(Table.REPORTS).apply(jdbc);

        performGetRequest("/reports")
                .andExpect(status().is5xxServerError())
                .andExpect(content().contentType(APPLICATION_JSON));
                //.andExpect(jsonPath("$.reportList").isArray())
                //.andExpect(jsonPath("$.reportList.length()").value(0));
    }

    @Test
    @SneakyThrows
    void shouldSuccessfullyReturn200WhenNoReportsFound() {
        jdbc.update("ALTER TABLE GPFD.REPORTS DROP CONSTRAINT fk_report_output_types_report_id");
        jdbc.update("ALTER TABLE GPFD.REPORT_GROUPS DROP CONSTRAINT fk_report_groups_report_id");
        jdbc.update("ALTER TABLE GPFD.REPORT_QUERIES DROP CONSTRAINT fk_report_queries_report_id");
        jdbc.update("ALTER TABLE GPFD.FIELD_ATTRIBUTES DROP CONSTRAINT fk_field_attributes_report_query_id");
        jdbc.update("ALTER TABLE GPFD.REPORTS_TRACKING DROP CONSTRAINT fk_reports_tracking_reports_id");
        jdbc.update("TRUNCATE TABLE GPFD.REPORTS");

        performGetRequest("/reports")
                .andExpect(status().is5xxServerError())
                .andExpect(content().contentType(APPLICATION_JSON));
                //.andExpect(jsonPath("$.reportList").isEmpty());
    }*/
}
