package uk.gov.laa.gpfd.integration;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import uk.gov.laa.gpfd.integration.verifier.DatabaseVerifier;
import uk.gov.laa.gpfd.integration.verifier.DatabaseVerifier.Table;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

final class GetReportsIT extends BaseIT {

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    @SneakyThrows
    void shouldSuccessfullyReturnAllAvailableReports() {
        var reportsLen = DatabaseVerifier.rowCountFor(Table.REPORTS).apply(jdbc);

        performGetRequestWithRoles("/reports", List.of("REP000", "Financial", "Reconciliation"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.reportList").isArray())
                .andExpect(jsonPath("$.reportList.length()").value(reportsLen));
    }

    @Test
    @SneakyThrows
    void shouldSuccessfullyReturn200WhenNoReportsFound() {
        jdbc.update("ALTER TABLE GPFD.REPORTS DROP CONSTRAINT fk_report_output_types_report_id");
        jdbc.update("ALTER TABLE GPFD.REPORT_GROUPS DROP CONSTRAINT fk_report_groups_report_id");
        jdbc.update("ALTER TABLE GPFD.REPORT_QUERIES DROP CONSTRAINT fk_report_queries_report_id");
        jdbc.update("ALTER TABLE GPFD.FIELD_ATTRIBUTES DROP CONSTRAINT fk_field_attributes_report_query_id");
        jdbc.update("TRUNCATE TABLE GPFD.REPORTS");

        performGetRequestWithRoles("/reports", List.of("REP000", "Reconciliation", "Financial"))
                .andExpect(status().isOk())
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.reportList").isEmpty());
    }

}
