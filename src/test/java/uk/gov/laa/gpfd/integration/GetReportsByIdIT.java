package uk.gov.laa.gpfd.integration;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.laa.gpfd.utils.FileUtils;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
@SpringBootTest
class GetReportsByIdIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate writeJdbcTemplate;

    @BeforeEach
    void setupDatabase() {
        String sqlSchema = FileUtils.readResourceToString("gpfd_schema.sql");
        String sqlData = FileUtils.readResourceToString("gpfd_data.sql");

        writeJdbcTemplate.execute(sqlSchema);
        writeJdbcTemplate.execute(sqlData);
    }

    @AfterEach
    void resetDatabase() {
        writeJdbcTemplate.execute("DROP TABLE IF EXISTS GPFD.REPORT_TRACKING");
        writeJdbcTemplate.execute("DROP TABLE IF EXISTS GPFD.CSV_TO_SQL_MAPPING_TABLE");
    }

    @Test
    void shouldReturnSingleReportWithMatchingId() throws Exception {
        MockHttpServletResponse response =  mockMvc.perform(get("/reports/1")
                .contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        Assertions.assertEquals(200, response.getStatus());

        var json = new JSONObject(response.getContentAsString());
        Assertions.assertEquals(1, json.get("id"));
    }

    @Test
    void shouldReturn400WhenGivenInvalidId() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/reports/1001")
                .contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        Assertions.assertEquals(400, response.getStatus());
    }

    @Test
    void shouldReturn404WhenNoReportsFound() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/reports/50")
                .contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        Assertions.assertEquals(404, response.getStatus());
    }
}


