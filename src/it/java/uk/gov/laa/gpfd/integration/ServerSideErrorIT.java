package uk.gov.laa.gpfd.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import uk.gov.laa.gpfd.testsupport.TestRoles;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ServerSideErrorIT extends BaseIT {

    @Autowired
    @Qualifier("trackingJdbcTemplate")
    private JdbcTemplate trackingJdbcTemplate;

    @BeforeAll
    @Override
    void setUpMojfinDatabase() {
        // Metadata now lives in tracking Postgres; remove schema so ReportDao reads fail.
        trackingJdbcTemplate.execute("DROP SCHEMA IF EXISTS glad CASCADE");
    }

    @AfterAll
    @Override
    void cleanUpMojfinDatabase() {
        // No-op: other IT classes use a fresh Postgres container per JVM.
    }

    @Test
    void getReportsShouldReturn500WhenCannotConnectToDb() throws Exception {
        performGetRequestWithRoles("/reports", TestRoles.all())
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getReportWithIdShouldReturn500WhenCannotConnectToDb() throws Exception {
        performGetRequestWithRoles("/reports/0d4da9ec-b0b3-4371-af10-f375330d85d9", TestRoles.all())
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getCsvWithIdShouldReturn500WhenCannotConnectToDbForMappingTable() throws Exception {
        performGetRequestWithRoles("/reports/0d4da9ec-b0b3-4371-af10-f375330d85d9/csv", TestRoles.all())
                .andExpect(status().isInternalServerError());
    }

}
