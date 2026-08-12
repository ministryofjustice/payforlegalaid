package uk.gov.laa.gpfd.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ServerSideErrorIT extends BaseIT {

    @Autowired
    private JdbcTemplate writeJdbcTemplate;

    @Autowired
    @Qualifier("metadataJdbcTemplate")
    private JdbcTemplate metadataJdbcTemplate;

    @BeforeAll
    @Override
    void setUpMojfinDatabase() {
        writeJdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS GPFD;");
    }

    @AfterAll
    @Override
    void cleanUpMojfinDatabase() {
        writeJdbcTemplate.execute("DROP SCHEMA IF EXISTS GPFD CASCADE");
    }

    @Test
    void getReportsShouldNotDependOnMojfinGpfdMetadataTables() throws Exception {
        try {
            performGetRequestWithRoles("/reports", List.of("Financial"))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            System.out.println("=== Test failed with exception: " + e.getMessage() + " ===");
            throw e;
        }
    }

    @Test
    void getReportByIdShouldNotDependOnMojfinGpfdMetadataTables() throws Exception {
        try {
            performGetRequestWithRoles("/reports/b36f9bbb-1178-432c-8f99-8090e285f2d3", List.of("Financial"))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            System.out.println("=== Test failed with exception: " + e.getMessage() + " ===");
            throw e;
        }
    }

}
