package uk.gov.laa.gpfd.integration;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application-test.yml")
class GetReportsIT extends BaseIT {

    public static final int NUMBER_OF_REPORTS_IN_TEST_DATA = 8;

    @Autowired
    private JdbcTemplate writeJdbcTemplate;

    @Test
    void shouldReturnListOfReports() throws Exception {
        performGetRequest("/reports")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.reportList").isArray())
            .andExpect(jsonPath("$.reportList.length()").value(NUMBER_OF_REPORTS_IN_TEST_DATA));
    }

    @Test
    void shouldReturn404WhenNoReportsFound() throws Exception {
        writeJdbcTemplate.update("ALTER TABLE GPFD.REPORTS DROP CONSTRAINT fk_report_output_types_report_id");
        writeJdbcTemplate.update("ALTER TABLE GPFD.REPORT_GROUPS DROP CONSTRAINT fk_report_groups_report_id");
        writeJdbcTemplate.update("ALTER TABLE GPFD.REPORT_QUERIES DROP CONSTRAINT fk_report_queries_report_id");
        writeJdbcTemplate.update("ALTER TABLE GPFD.FIELD_ATTRIBUTES DROP CONSTRAINT fk_field_attributes_report_query_id");
        writeJdbcTemplate.update("ALTER TABLE GPFD.REPORTS_TRACKING DROP CONSTRAINT fk_reports_tracking_reports_id");
        writeJdbcTemplate.update("TRUNCATE TABLE GPFD.REPORTS");

        performGetRequest("/reports")
            .andExpect(status().is5xxServerError());
    }
}