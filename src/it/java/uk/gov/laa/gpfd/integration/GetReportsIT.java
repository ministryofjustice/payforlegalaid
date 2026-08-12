package uk.gov.laa.gpfd.integration;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    @Qualifier("metadataJdbcTemplate")
    private JdbcTemplate metadataJdbcTemplate;

    @Test
    @SneakyThrows
    void shouldSuccessfullyReturnAllAvailableReports() {
        var reportsLen = DatabaseVerifier.rowCountFor(Table.REPORTS).apply(metadataJdbcTemplate);

        performGetRequestWithRoles("/reports", List.of("REP000", "Financial", "Reconciliation"))
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
