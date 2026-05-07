package uk.gov.laa.gpfd.integration;

import lombok.SneakyThrows;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.MvcResult;
import uk.gov.laa.gpfd.config.TestDatabaseConfig;
import uk.gov.laa.gpfd.integration.config.OAuth2TestConfig;
import uk.gov.laa.gpfd.integration.data.ReportTestData;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.laa.gpfd.integration.data.ReportTestData.ReportType.CCMS_THIRD_PARTY_REPORT;

@SpringBootTest(
        webEnvironment = RANDOM_PORT,
        classes = {TestDatabaseConfig.class, OAuth2TestConfig.class}
)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(PER_CLASS)
@TestPropertySource(locations = "classpath:application-test.yml")
final class ExcelReportStreamingIT extends BaseIT {

    private static final String EXCEL_CONTENT_TYPE =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    static Stream<ReportTestData> excelReports() {
        return Stream.of(
//                CCMS_REPORT.getReportData(),
//                GENERAL_LEDGER_REPORT.getReportData(),
//                CCMS_AND_CIS_BANK_ACCOUNT_REPORT.getReportData(),
//                LEGAL_HELP_CONTRACT_BALANCES_REPORT.getReportData(),
//                AGFS_LATE_PROCESSED_BILLS_REPORT.getReportData(),
                CCMS_THIRD_PARTY_REPORT.getReportData()
        );
    }

    @SneakyThrows
    @ParameterizedTest(name = "{0} should stream a valid Excel file with correct content type")
    @MethodSource("excelReports")
    void shouldStreamExcelFileWithCorrectContentType(ReportTestData report) {
        var mvcResult = performExcelRequest(report.id())
                .andExpect(request().asyncStarted())
                .andReturn();

        var result = mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(content().contentType(EXCEL_CONTENT_TYPE))
                .andReturn();

        assertNull(result.getResolvedException());
    }

    @SneakyThrows
    @ParameterizedTest(name = "{0} should stream a readable Excel file")
    @MethodSource("excelReports")
    void shouldStreamAValidReadableExcelFile(ReportTestData report) {
        var workbook = getWorkbook(report.id());

        assertNotNull(workbook);
        assertTrue(workbook.getNumberOfSheets() > 0);
    }

    @SneakyThrows
    @ParameterizedTest(name = "{0} should have named sheets")
    @MethodSource("excelReports")
    void shouldStreamExcelFileWithExpectedSheets(ReportTestData report) {
        var workbook = getWorkbook(report.id());

        var sheet = workbook.getSheetAt(0);
        assertNotNull(sheet);
        assertNotNull(sheet.getSheetName());
    }

    @SneakyThrows
    @ParameterizedTest(name = "{0} should have data in cells")
    @MethodSource("excelReports")
    void shouldStreamExcelFileWithDataInCells(ReportTestData report) {
        var workbook = getWorkbook(report.id());
        var sheet = workbook.getSheetAt(0);

        assertTrue(sheet.getPhysicalNumberOfRows() > 0);
        assertNotNull(sheet.getRow(0));
        assertTrue(sheet.getRow(0).getPhysicalNumberOfCells() > 0);
    }

    @SneakyThrows
    @ParameterizedTest(name = "{0} should have rows in correct order")
    @MethodSource("excelReports")
    void shouldStreamExcelFileWithRowsInCorrectOrder(ReportTestData report) {
        var workbook = getWorkbook(report.id());
        var sheet = workbook.getSheetAt(0);

        int lastRowNum = -1;
        for (var row : sheet) {
            assertTrue(
                    row.getRowNum() > lastRowNum,
                    "Rows out of order — row %d appeared after row %d. Likely means _rownum reflection is broken."
                            .formatted(row.getRowNum(), lastRowNum)
            );
            lastRowNum = row.getRowNum();
        }
    }

    @SneakyThrows
    private XSSFWorkbook getWorkbook(String reportId) {
        var bytes = performExcelDownload(reportId);

        assertTrue(bytes.length > 0, "Expected Excel bytes but response was empty");

        return new XSSFWorkbook(new ByteArrayInputStream(bytes));
    }

    @SneakyThrows
    private byte[] performExcelDownload(String reportId) {
        var mvcResult = performExcelRequest(reportId)
                .andExpect(request().asyncStarted())
                .andReturn();

        MvcResult result = mockMvc.perform(asyncDispatch(mvcResult))
                .andReturn();

        // This is the important bit: surface the real underlying exception
        if (result.getResolvedException() != null) {
            result.getResolvedException().printStackTrace();
        }

        assertNull(
                result.getResolvedException(),
                "Excel stream failed with exception: " +
                        (result.getResolvedException() != null
                                ? result.getResolvedException().getMessage()
                                : "unknown")
        );

        assertTrue(
                result.getResponse().getStatus() == 200,
                "Expected HTTP 200 but got " + result.getResponse().getStatus()
        );

        return result.getResponse().getContentAsByteArray();
    }

    @SneakyThrows
    private ResultActions performExcelRequest(String reportId) {
        var auth = mock(Authentication.class);
        var principal = mock(DefaultOidcUser.class);

        when(auth.getPrincipal()).thenReturn(principal);
        when(principal.getClaimAsStringList("groups")).thenReturn(List.of());

        var mockSecurityContext = mock(SecurityContext.class);
        when(mockSecurityContext.getAuthentication()).thenReturn(auth);

        return performGetRequest("/reports/" + reportId + "/excel", mockSecurityContext);
    }
}