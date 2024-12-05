package uk.gov.laa.gpfd.integration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.laa.gpfd.utils.FileUtils;

import static org.junit.Assert.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
    @ActiveProfiles("local") // Use application-test.yml configuration
    public class GetRecordsTest {

        @Autowired
        private TestRestTemplate restTemplate;

        @Autowired
        private JdbcTemplate jdbcTemplate;

        @BeforeEach
        public void setupData() {

            String sqlSchema = FileUtils.readResourceToString("schema.sql");
            String sqlData = FileUtils.readResourceToString("data.sql");

            jdbcTemplate.execute(sqlSchema);
            jdbcTemplate.execute(sqlData);

        }

        @Test
        public void testResultsEndpointReturnsData() {
            // Call the /results endpoint
            ResponseEntity<String> response = restTemplate.getForEntity("/reports", String.class);

            // Assert HTTP status code
            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

            // Assert response body contains the expected data
            String responseBody = response.getBody();
            Assertions.assertNotNull(responseBody);
            Assertions.assertTrue(responseBody.contains("Result A"));
            Assertions.assertTrue(responseBody.contains("Result B"));
        }
    }

