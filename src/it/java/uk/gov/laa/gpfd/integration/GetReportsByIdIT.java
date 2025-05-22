package uk.gov.laa.gpfd.integration;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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

    private static final String CCMS_REPORT = "b36f9bbb-1178-432c-8f99-8090e285f2d3";
    private static final String GENERAL_LEDGER_REPORT = "f46b4d3d-c100-429a-bf9a-223305dbdbfb";
    private static final String CCMS_AND_CIS_BANK_ACCOUNT_REPORT_W_CATEGORY_CODE_YTD_REPORT = "eee30b23-2c8d-4b4b-bb11-8cd67d07915c";
    private static final String LEGAL_HELP_CONTRACT_BALANCES_REPORT = "7073dd13-e325-4863-a05c-a049a815d1f7";
    private static final String AGFS_LATE_PROCESSED_BILLS_REPORT = "7bda9aa4-6129-4c71-bd12-7d4e46fdd882";
    private static final String CCLF_LATE_PROCESSED_BILLS_REPORT = "516cdbff-5fa8-4050-b5e6-7edf71daf679";

    private static final String INITIAL_TEST_REPORT = "0d4da9ec-b0b3-4371-af10-f375330d85d3";

    @ParameterizedTest
    @ValueSource(strings = {
            INITIAL_TEST_REPORT,
    })
    void givenCsvReportId_whenSingleReportRequested_thenCsvUrlReturned(String id) throws Exception {
        performGetRequest("/reports/" + id)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.reportDownloadUrl")
                        .value("http://localhost/csv/" + id));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            CCMS_REPORT,
            GENERAL_LEDGER_REPORT,
            CCMS_AND_CIS_BANK_ACCOUNT_REPORT_W_CATEGORY_CODE_YTD_REPORT,
            LEGAL_HELP_CONTRACT_BALANCES_REPORT, AGFS_LATE_PROCESSED_BILLS_REPORT, CCLF_LATE_PROCESSED_BILLS_REPORT
    })
    void givenExcelReportId_whenSingleReportRequested_thenExcelUrlReturned(String id) throws Exception {
        performGetRequest("/reports/" + id)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.reportDownloadUrl")
                        .value("http://localhost/excel/" + id));
    }

    @Test
    void shouldReturn400WhenGivenInvalidId() throws Exception {
        performGetRequest("/reports/" + BaseIT.REPORT_UUID_1 + "321")
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "excel",
            "csv"
    })
    void shouldReturn404WhenNoReportsFound(String type) throws Exception {
        performGetRequest("/%s/0d4da9ec-b0b3-4371-af10-321".formatted(type))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenABrandNewReportOutputType_whenSingleReportRequested_thenInternalServerError() throws Exception {
        performGetRequest("/reports/b36f9bbb-1178-432c-8f99-8090e285f2d4")
                .andExpect(status().isInternalServerError());
    }
}


