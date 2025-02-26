package uk.gov.laa.gpfd.integration;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application-test.yml")
class GetReportsIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate writeJdbcTemplate;

    @BeforeAll
    void setupDatabase() {
        String sqlSchema = FileUtils.readResourceToString("gpfd_schema.sql");
        String sqlData = FileUtils.readResourceToString("gpfd_data.sql");
        String sqlReportSchema = FileUtils.readResourceToString("gpfd_reports_schema.sql");
        String sqlReportData = FileUtils.readResourceToString("gpfd_reports_data.sql");

        writeJdbcTemplate.execute(sqlSchema);
        writeJdbcTemplate.execute(sqlData);
        writeJdbcTemplate.execute(sqlReportSchema);
        writeJdbcTemplate.execute(sqlReportData);
    }

    @AfterAll
    void resetDatabase() {
        writeJdbcTemplate.execute("DROP TABLE IF EXISTS GPFD.REPORT_TRACKING");
        writeJdbcTemplate.execute("DROP TABLE IF EXISTS GPFD.CSV_TO_SQL_MAPPING_TABLE");
        writeJdbcTemplate.execute("DROP TABLE GPFD.REPORTS_TRACKING");
        writeJdbcTemplate.execute("DROP TABLE GPFD.FIELD_ATTRIBUTES");
        writeJdbcTemplate.execute("DROP TABLE GPFD.REPORT_QUERIES");
        writeJdbcTemplate.execute("DROP TABLE GPFD.REPORT_GROUPS");
        writeJdbcTemplate.execute("DROP TABLE GPFD.REPORTS");
        writeJdbcTemplate.execute("DROP TABLE GPFD.REPORT_OUTPUT_TYPES");
    }

    @Test
    void shouldReturnListOfReports() throws Exception {
        MockHttpServletResponse response =  mockMvc.perform(get("/reports")
                .contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        Assertions.assertEquals(200, response.getStatus());

        var json = new JSONObject(response.getContentAsString());
        JSONArray reportList = (JSONArray) json.get("reportList");
        Assertions.assertEquals(3, reportList.toList().size());
    }

    @Test
    void shouldReturn404WhenNoReportsFound() throws Exception {
        writeJdbcTemplate.update("TRUNCATE TABLE GPFD.CSV_TO_SQL_MAPPING_TABLE");

        MockHttpServletResponse response = mockMvc.perform(get("/reports")
                .contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        Assertions.assertEquals(404, response.getStatus());
    }
}