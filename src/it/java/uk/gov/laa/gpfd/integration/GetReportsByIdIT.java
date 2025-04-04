package uk.gov.laa.gpfd.integration;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(OAuth2TestConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application-test.yml")
class GetReportsByIdIT extends BaseIT {

    @Test
    void givenCsvReportId_whenSingleReportRequested_thenCsvUrlReturned() throws Exception {
        performGetRequest("/reports/0d4da9ec-b0b3-4371-af10-f375330d85d3")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("0d4da9ec-b0b3-4371-af10-f375330d85d3"))
            .andExpect(jsonPath("$.reportDownloadUrl").value("http://localhost/csv/0d4da9ec-b0b3-4371-af10-f375330d85d3"));
    }

    @Test
    void givenExcelReportId_whenSingleReportRequested_thenExcelUrlReturned() throws Exception {
        performGetRequest("/reports/b36f9bbb-1178-432c-8f99-8090e285f2d3")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("b36f9bbb-1178-432c-8f99-8090e285f2d3"))
            .andExpect(jsonPath("$.reportDownloadUrl").value("http://localhost/excel/b36f9bbb-1178-432c-8f99-8090e285f2d3"));
    }

    @Test
    void shouldReturn400WhenGivenInvalidId() throws Exception {
        performGetRequest("/reports/0d4da9ec-b0b3-4371-af10-f375330d85d3321")
            .andExpect(status().isBadRequest());
    }

    @Test
    void givenABrandNewReportOutputType_whenSingleReportRequested_thenInternalServerError() throws Exception {
        performGetRequest("/reports/b36f9bbb-1178-432c-8f99-8090e285f2d4")
            .andExpect(status().isInternalServerError());
    }
}


