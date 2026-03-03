package uk.gov.laa.gpfd.integration;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import uk.gov.laa.gpfd.config.TestDatabaseConfig;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {
                TestDatabaseConfig.class
        }
)
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
        performGetRequest("/reports")
            .andExpect(status().isInternalServerError());
    }

    @Test
    void getReportWithIdShouldReturn500WhenCannotConnectToDb() throws Exception {
        performGetRequest("/reports/0d4da9ec-b0b3-4371-af10-f375330d85d9")
            .andExpect(status().isInternalServerError());
    }

    @Test
    void getCsvWithIdShouldReturn500WhenCannotConnectToDbForMappingTable() throws Exception {
        performGetRequest("/csv/0d4da9ec-b0b3-4371-af10-f375330d85d9")
            .andExpect(status().isInternalServerError());
    }

}