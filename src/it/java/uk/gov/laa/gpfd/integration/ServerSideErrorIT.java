package uk.gov.laa.gpfd.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application-test.yml")
class ServerSideErrorIT extends BaseIT {

    @Autowired
    private JdbcTemplate writeJdbcTemplate;

    @BeforeAll
    @Override
    void setUpDatabase() {
        writeJdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS GPFD;"); //Create an empty schema so that we get a 500 error
    }

    @AfterAll
    @Override
    void cleanUpDatabase() {
        writeJdbcTemplate.execute("DROP TABLE IF EXISTS GPFD.REPORTS_TRACKING");
        writeJdbcTemplate.execute("DROP TABLE IF EXISTS GPFD.CSV_TO_SQL_MAPPING_TABLE");
    }

    @Test
    void getReportsShouldReturn500WhenCannotConnectToDb() throws Exception {
        MockHttpServletResponse response = performGetRequest("/reports");

        Assertions.assertEquals(500, response.getStatus());
    }

    @Test
    void getReportWithIdShouldReturn500WhenCannotConnectToDb() throws Exception {
        MockHttpServletResponse response = performGetRequest("/reports/0d4da9ec-b0b3-4371-af10-f375330d85d9");

        Assertions.assertEquals(500, response.getStatus());
    }

    @Test
    void getCsvWithIdShouldReturn500WhenCannotConnectToDbForMappingTable() throws Exception {
        MockHttpServletResponse response = performGetRequest("/csv/0d4da9ec-b0b3-4371-af10-f375330d85d9");

        Assertions.assertEquals(500, response.getStatus());
    }

    @Test
    void getCsvWithIdShouldReturn500WhenCannotConnectToDbForReportTable() throws Exception {
        MockHttpServletResponse response = performGetRequest("/csv/0d4da9ec-b0b3-4371-af10-f375330d85d9");

        Assertions.assertEquals(500, response.getStatus());
    }

}