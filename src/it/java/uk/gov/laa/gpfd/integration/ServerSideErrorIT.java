package uk.gov.laa.gpfd.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ServerSideErrorIT extends BaseIT {

    @Autowired
    private JdbcTemplate writeJdbcTemplate;

    @BeforeAll
    @Override
    void setUpMojfinDatabase() {
        writeJdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS GPFD;"); //Create an empty schema so that we get a 500 error
    }

    @AfterAll
    @Override
    void cleanUpMojfinDatabase() {
        writeJdbcTemplate.execute("DROP TABLE IF EXISTS GPFD.CSV_TO_SQL_MAPPING_TABLE");
    }

    @Test
    void getReportsShouldReturn500WhenCannotConnectToDb() throws Exception {
        performGetRequestWithRoles("/reports", List.of("Financial"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getReportWithIdShouldReturn500WhenCannotConnectToDb() throws Exception {
        performGetRequestWithRoles("/reports/0d4da9ec-b0b3-4371-af10-f375330d85d9", List.of("Financial"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getCsvWithIdShouldReturn500WhenCannotConnectToDbForMappingTable() throws Exception {
        performGetRequestWithRoles("/reports/0d4da9ec-b0b3-4371-af10-f375330d85d9/csv", List.of("Financial"))
                .andExpect(status().isInternalServerError());
    }

}