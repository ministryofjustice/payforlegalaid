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
    void givenCsvReportId_whenSingleReportRequested_thenCsvUrlReturned() throws Exception {
        MockHttpServletResponse response =  mockMvc.perform(get("/reports/0d4da9ec-b0b3-4371-af10-f375330d85d3")
                .contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        Assertions.assertEquals(200, response.getStatus());

        var json = new JSONObject(response.getContentAsString());
        Assertions.assertEquals("0d4da9ec-b0b3-4371-af10-f375330d85d3", json.get("id"));
        Assertions.assertEquals("http://localhost/csv/0d4da9ec-b0b3-4371-af10-f375330d85d3", json.get("reportDownloadUrl"));
    }

    @Test
    void givenExcelReportId_whenSingleReportRequested_thenExcelUrlReturned() throws Exception {
        MockHttpServletResponse response =  mockMvc.perform(get("/reports/b36f9bbb-1178-432c-8f99-8090e285f2d3")
            .contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        Assertions.assertEquals(200, response.getStatus());

        var json = new JSONObject(response.getContentAsString());
        Assertions.assertEquals("b36f9bbb-1178-432c-8f99-8090e285f2d3", json.get("id"));
        Assertions.assertEquals("http://localhost/excel/b36f9bbb-1178-432c-8f99-8090e285f2d3", json.get("reportDownloadUrl"));
    }

    @Test
    void shouldReturn400WhenGivenInvalidId() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/reports/0d4da9ec-b0b3-4371-af10-f375330d85d3321")
                .contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        Assertions.assertEquals(400, response.getStatus());
    }

    @Test
    void givenABrandNewReportOutputType_whenSingleReportRequested_thenInvalidUrlReturned() throws Exception {
        MockHttpServletResponse response =  mockMvc.perform(get("/reports/b36f9bbb-1178-432c-8f99-8090e285f2d4")
            .contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        Assertions.assertEquals(200, response.getStatus());

        var json = new JSONObject(response.getContentAsString());
        Assertions.assertEquals("b36f9bbb-1178-432c-8f99-8090e285f2d4", json.get("id"));
        Assertions.assertEquals("New report with a new output type", json.get("reportName"));
        Assertions.assertEquals("http://localhost/invalid/b36f9bbb-1178-432c-8f99-8090e285f2d4", json.get("reportDownloadUrl"));
    }
}


