package uk.gov.laa.gpfd.integration;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.laa.gpfd.security.SilasRoles.all;

final class GetReportsIT extends BaseIT {

    private static final List<String> REPORT_ROLES = all();

    private static final String COUNT_ACCESSIBLE_REPORTS_SQL = """
            SELECT COUNT(DISTINCT r.id)
            FROM glad.reports r
            INNER JOIN glad.report_roles rr ON r.id = rr.report_id
            INNER JOIN glad.roles ro ON rr.role_id = ro.role_id
            WHERE r.active = 'Y' AND ro.role_name IN (:roles)
            """;

    @Autowired
    @Qualifier("metadataClient")
    private JdbcClient metadataClient;

    @Test
    @SneakyThrows
    void shouldSuccessfullyReturnAllAvailableReports() {
        var reportsLen = metadataClient.sql(COUNT_ACCESSIBLE_REPORTS_SQL)
                .param("roles", REPORT_ROLES)
                .query(Integer.class)
                .single();

        performGetRequestWithRoles("/reports", REPORT_ROLES)
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.reportList").isArray())
                .andExpect(jsonPath("$.reportList.length()").value(reportsLen));
    }

    @Test
    @SneakyThrows
    void shouldSuccessfullyReturn200WhenNoReportsFound() {
        performGetRequestWithRoles("/reports", List.of("not-a-report-role"))
                .andExpect(status().isOk())
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.reportList").isEmpty());
    }

}
