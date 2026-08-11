package uk.gov.laa.gpfd.integration;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import uk.gov.laa.gpfd.testsupport.TestRoles;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

final class GetReportsIT extends BaseIT {

    @Autowired
    @Qualifier("trackingJdbcTemplate")
    private JdbcTemplate trackingJdbc;

    @Test
    @SneakyThrows
    void shouldSuccessfullyReturnAllAvailableReports() {
        var reportsLen = trackingJdbc.queryForObject("""
                SELECT COUNT(DISTINCT r.id)
                FROM glad.reports r
                INNER JOIN glad.report_roles rr ON r.id = rr.report_id
                INNER JOIN glad.roles ro ON rr.role_id = ro.role_id
                WHERE r.active = 'Y'
                  AND ro.role_name IN (?, ?, ?)
                """, Long.class, TestRoles.REP000, TestRoles.RECONCILIATION, TestRoles.FINANCIAL);

        performGetRequestWithRoles("/reports", TestRoles.all())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.reportList").isArray())
                .andExpect(jsonPath("$.reportList.length()").value(reportsLen));
    }

    @Test
    @SneakyThrows
    void shouldSuccessfullyReturn200WhenNoReportsFound() {
        trackingJdbc.update("TRUNCATE glad.reports CASCADE");

        performGetRequestWithRoles("/reports", TestRoles.all())
                .andExpect(status().isOk())
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.reportList").isEmpty());
    }

}
