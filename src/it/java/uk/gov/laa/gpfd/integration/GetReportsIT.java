package uk.gov.laa.gpfd.integration;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;
import java.util.Map;

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
    @Qualifier("namedMetadataJdbcTemplate")
    private NamedParameterJdbcTemplate namedMetadataJdbcTemplate;

    @Autowired
    @Qualifier("metadataJdbcTemplate")
    private JdbcTemplate metadataJdbcTemplate;

    @Test
    @SneakyThrows
    void shouldSuccessfullyReturnAllAvailableReports() {
        var reportsLen = namedMetadataJdbcTemplate.queryForObject(
                COUNT_ACCESSIBLE_REPORTS_SQL,
                Map.of("roles", REPORT_ROLES),
                Integer.class
        );

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
