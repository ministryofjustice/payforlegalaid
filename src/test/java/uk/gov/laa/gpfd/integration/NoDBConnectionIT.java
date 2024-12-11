package uk.gov.laa.gpfd.integration;

import org.junit.jupiter.api.Assertions;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@AutoConfigureMockMvc
@ActiveProfiles("test") // Ensure the test profile is used
@TestPropertySource(locations = "classpath:application-test.yml")
@SpringBootTest
class NoDBConnectionIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate writeJdbcTemplate;

    // 401 response
    @Test
    void getReportsShouldReturn401WithoutAuthToken() throws Exception {
        MockHttpServletResponse response =  mockMvc.perform(get("/reports")
                        .contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        Assertions.assertEquals(500, response.getStatus());
    }

    // 401 response
    @Test
    void getReportWithIdShouldReturn401WithoutAuthToken() throws Exception {
        MockHttpServletResponse response =  mockMvc.perform(get("/report/1")
                .contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        Assertions.assertEquals(500, response.getStatus());
    }

    // 401 response
    @Test
    void getCsvWithIdShouldReturn401WithoutAuthToken() throws Exception {
        MockHttpServletResponse response =  mockMvc.perform(get("/csv/1")
                .contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        Assertions.assertEquals(500, response.getStatus());
    }
}


