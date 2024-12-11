package uk.gov.laa.gpfd.integration;

import org.json.JSONArray;
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
@ActiveProfiles("testauth") // Ensure the test profile is used
@TestPropertySource(locations = "classpath:application-testauth.yml")
@SpringBootTest
class NoAuthTokenIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate writeJdbcTemplate;

    // 401 response
    @Test
    void getReportsShouldReturn401WithoutAuthToken() throws Exception {
        MockHttpServletResponse response =  mockMvc.perform(get("/reports")
                        .contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        Assertions.assertEquals(401, response.getStatus());
    }

    // 401 response
    @Test
    void getReportWithIdShouldReturn401WithoutAuthToken() throws Exception {
        MockHttpServletResponse response =  mockMvc.perform(get("/report/1")
                .contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        Assertions.assertEquals(401, response.getStatus());
    }

    // 401 response
    @Test
    void getCsvWithIdShouldReturn401WithoutAuthToken() throws Exception {
        MockHttpServletResponse response =  mockMvc.perform(get("/csv/1")
                .contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        Assertions.assertEquals(401, response.getStatus());
    }
}


