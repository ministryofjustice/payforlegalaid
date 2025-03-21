package uk.gov.laa.gpfd.integration;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(OAuth2TestConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application-test.yml")
class GetReportsByIdIT extends BaseIT {

    @Test
    void shouldReturnSingleReportWithMatchingId() throws Exception {
        MockHttpServletResponse response =  mockMvc.perform(get("/reports/0d4da9ec-b0b3-4371-af10-f375330d85d3")
                .contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        Assertions.assertEquals(200, response.getStatus());

        var json = new JSONObject(response.getContentAsString());
        Assertions.assertEquals("0d4da9ec-b0b3-4371-af10-f375330d85d3", json.get("id"));
    }

    @Test
    void shouldReturn400WhenGivenInvalidId() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/reports/0d4da9ec-b0b3-4371-af10-f375330d85d3321")
                .contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        Assertions.assertEquals(400, response.getStatus());
    }
}


