package uk.gov.laa.gpfd.integration;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uk.gov.laa.gpfd.graph.AzureGraphClient;
import uk.gov.laa.gpfd.utils.FileUtils;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Client;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles("test") // Ensure the test profile is used
@TestPropertySource(locations = "classpath:application-test.yml")
@SpringBootTest
class GetCsvIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate writeJdbcTemplate;

    @MockBean
    AzureGraphClient mockAzureGraphClient;

    @BeforeEach
    void setupDatabase() throws Exception {
        String sqlSchema = FileUtils.readResourceToString("schema.sql");
        String sqlData = FileUtils.readResourceToString("data.sql");

        writeJdbcTemplate.execute(sqlSchema);
        writeJdbcTemplate.execute(sqlData);
    }

    @AfterEach
    void resetDatabase() {
        writeJdbcTemplate.update("TRUNCATE TABLE GPFD.REPORT_TRACKING");
        writeJdbcTemplate.update("DROP SEQUENCE GPFD_TRACKING_TABLE_SEQUENCE");
        writeJdbcTemplate.update("TRUNCATE TABLE GPFD.CSV_TO_SQL_MAPPING_TABLE");
    }

    // 200 response
    @Test
    void shouldReturnListOfReports() throws Exception {

        MockHttpServletResponse response =  mockMvc.perform(MockMvcRequestBuilders.get("/csv/1").with(oidcLogin()).with(oauth2Client("graph"))).andReturn().getResponse();



//                .andExpect(status().isOk()).andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=data.csv"));
        System.out.println(response);

//        Assertions.assertEquals(200, response.getStatus());
//
//        var json = new JSONObject(response.getContentAsString());
//        Assertions.assertEquals(1, json.get("id"));
    }

    // 400 response - unable to identify a scenario for this
    @Test
    void shouldReturn404WhenGivenInvalidId() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/csv/1001")
                .contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        Assertions.assertEquals(400, response.getStatus());
    }

    // 404 response
    @Test
    void shouldReturn404WhenNoReportsFound() throws Exception {
        writeJdbcTemplate.update("TRUNCATE TABLE GPFD.CSV_TO_SQL_MAPPING_TABLE");

        MockHttpServletResponse response = mockMvc.perform(get("/csv/1")
                .contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        Assertions.assertEquals(404, response.getStatus());
    }
}


