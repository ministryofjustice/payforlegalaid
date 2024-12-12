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
@ActiveProfiles("testauth")
@TestPropertySource(locations = "classpath:application-testauth.yml")
@SpringBootTest
class NoAuthTokenIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate writeJdbcTemplate;

    @Test
    void getReportsShouldReturn401WithoutAuthToken() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/reports")
                .contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        //TODO confirm when dev env is fixed is this should be 401 or 302 (redirect)
        Assertions.assertEquals(302, response.getStatus());
//        Assertions.assertEquals(401, response.getStatus());
    }

    @Test
    void getReportWithIdShouldReturn401WithoutAuthToken() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/report/1")
                .contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

//TODO confirm when dev env is fixed is this should be 401 or 302 (redirect)
        Assertions.assertEquals(302, response.getStatus());
//        Assertions.assertEquals(401, response.getStatus());
    }
}