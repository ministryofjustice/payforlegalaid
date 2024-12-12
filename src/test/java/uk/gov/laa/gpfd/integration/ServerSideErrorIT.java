package uk.gov.laa.gpfd.integration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
@SpringBootTest
class ServerSideErrorIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getReportsShouldReturn500WhenCannotConnectToDb() throws Exception {
        MockHttpServletResponse response =  mockMvc.perform(get("/reports")
                .contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        Assertions.assertEquals(500, response.getStatus());
    }

    @Test
    void getReportWithIdShouldReturn500WhenCannotConnectToDb() throws Exception {
        MockHttpServletResponse response =  mockMvc.perform(get("/reports/1")
                .contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        Assertions.assertEquals(500, response.getStatus());
    }
}