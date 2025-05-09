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

    public static final String REPORT_UUID_1 = "b36f9bbb-1178-432c-8f99-8090e285f2d3";

    @Test
    void givenCsvReportId_whenSingleReportRequested_thenCsvUrlReturned() throws Exception {
        performGetRequest("/reports/" + BaseIT.REPORT_UUID_1)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(BaseIT.REPORT_UUID_1))
            .andExpect(jsonPath("$.reportDownloadUrl").value("http://localhost/csv/" + BaseIT.REPORT_UUID_1));
    }

    @Test
    void givenExcelReportId_whenSingleReportRequested_thenExcelUrlReturned() throws Exception {
        performGetRequest("/reports/" + REPORT_UUID_1)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(REPORT_UUID_1))
            .andExpect(jsonPath("$.reportDownloadUrl").value(
                "http://localhost/excel/" + REPORT_UUID_1));
    }

    @Test
    void shouldReturn400WhenGivenInvalidId() throws Exception {
        performGetRequest("/reports/" + BaseIT.REPORT_UUID_1 + "321")
            .andExpect(status().isBadRequest());
    }

    @Test
    void givenABrandNewReportOutputType_whenSingleReportRequested_thenInternalServerError() throws Exception {
        performGetRequest("/reports/b36f9bbb-1178-432c-8f99-8090e285f2d4")
            .andExpect(status().isInternalServerError());
    }
}


