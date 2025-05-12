package uk.gov.laa.gpfd.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.laa.gpfd.builders.ReportResponseTestBuilder;
import uk.gov.laa.gpfd.data.ReportDetailsTestDataFactory;
import uk.gov.laa.gpfd.data.ReportsTestDataFactory;
import uk.gov.laa.gpfd.exception.SqlFormatException;
import uk.gov.laa.gpfd.model.GetReportById200Response;
import uk.gov.laa.gpfd.model.ReportDetails;
import uk.gov.laa.gpfd.services.DataStreamer;
import uk.gov.laa.gpfd.services.MappingTableService;
import uk.gov.laa.gpfd.services.ReportManagementService;
import uk.gov.laa.gpfd.services.ReportService;
import uk.gov.laa.gpfd.utils.SqlFormatValidator;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.laa.gpfd.data.MappingTableTestDataFactory.aValidInvoiceAnalysisReport;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {
    private static final UUID VALID_REPORT_ID = UUID.fromString("0d4da9ec-b0b3-4371-af10-f375330d85d1");
    @Mock
    DataStreamer dataStreamer;

    @Mock
    MappingTableService mappingTableService;

    @Mock
    SqlFormatValidator sqlFormatValidator;

    @Mock
    ReportManagementService reportManagementService;
    @InjectMocks
    ReportService reportService;
    List<Map<String, Object>> reportMapMockList = new ArrayList<>();

    @BeforeEach
    void init() {
        Map<String, Object> rowOne = new LinkedHashMap<>();
        rowOne.put("name", "CCMS Report");
        rowOne.put("balance", 12300);
        rowOne.put("system", "ccms");
        reportMapMockList.add(rowOne);

        Map<String, Object> rowTwo = new LinkedHashMap<>();
        rowTwo.put("name", "CCMS Report2");
        rowTwo.put("balance", 16300);
        rowTwo.put("system", "ccms");
        reportMapMockList.add(rowTwo);
    }

    @Test
    void createCSVResponse_ShouldGenerateCorrectCSVContent() throws Exception {
        var expectedCsvHeader = "id,date,report";
        var expectedRow1 = "1,2023-08-07 00:00:00.0,Example Report 1";
        var expectedRow2 = "2,2023-12-31 01:50:00.0,Example Report 2";
        var testId = UUID.randomUUID();

        when(mappingTableService.getDetailsForSpecificMapping(testId)).thenReturn(aValidInvoiceAnalysisReport());
        when(sqlFormatValidator.isSqlFormatValid(any())).thenReturn(true);

        doAnswer(invocation -> {
            String csvContent = expectedCsvHeader + "\n" + expectedRow1 + "\n" + expectedRow2;
            OutputStream stream = invocation.getArgument(1);
            stream.write(csvContent.getBytes());
            return null;
        }).when(dataStreamer).stream(any(String.class), any(OutputStream.class));

        var response = reportService.createCSVResponse(testId);

        var outputStream = new ByteArrayOutputStream();
        Objects.requireNonNull(response.getBody()).writeTo(outputStream);
        var csvContent = outputStream.toString();
        Assertions.assertNotNull(csvContent);
        assertTrue(csvContent.contains(expectedCsvHeader));
        assertTrue(csvContent.contains(expectedRow1));
        assertTrue(csvContent.contains(expectedRow2));
    }

    @Test
    void createCSVResponse_GivenInvalidSql_ShouldThrowException() throws Exception {
        var testId = UUID.randomUUID();

        when(mappingTableService.getDetailsForSpecificMapping(testId)).thenReturn(aValidInvoiceAnalysisReport());
        when(sqlFormatValidator.isSqlFormatValid(any())).thenReturn(false);

        assertThrows(SqlFormatException.class, () -> reportService.createCSVResponse(testId));

        verify(dataStreamer, times(0)).stream(any(), any());

    }

    @Test
    void createReportResponse_reportResponseMatchesValuesFromMappingTable() {

        // Mocking the data from mapping table

        GetReportById200Response expectedReportResponse = new ReportResponseTestBuilder().createReportResponse();
        ReportDetails mockReportDetails = ReportDetailsTestDataFactory.aValidReportResponse(
                VALID_REPORT_ID, "Excel_Report_Name-CSV-NAME-sheetnumber", "csv");
        when(reportManagementService.getDetailsForSpecificReport(VALID_REPORT_ID)).thenReturn(mockReportDetails);

        //Act
        GetReportById200Response actualReportResponse = reportService.createReportResponse(VALID_REPORT_ID);

        //check something
        assertEquals(expectedReportResponse.getReportName(), actualReportResponse.getReportName());
        assertEquals(expectedReportResponse.getId(), actualReportResponse.getId());

    }


    @Test
    void givenValidId_whenCreateReportResponse_thenValidResponseIsReturned() {
        //Given
        ReportDetails mockReportDetails = ReportDetailsTestDataFactory.aValidReportResponse(
                VALID_REPORT_ID, "Test Report", "csv");
        when(reportManagementService.getDetailsForSpecificReport(VALID_REPORT_ID)).thenReturn(mockReportDetails);
        //When
        GetReportById200Response actualReportResponse = reportService.createReportResponse(
                VALID_REPORT_ID);
        //then
        assertEquals("Test Report", actualReportResponse.getReportName());
        assertEquals(VALID_REPORT_ID, actualReportResponse.getId());
        assertEquals(ReportsTestDataFactory.TEST_DOWNLOAD_URL, actualReportResponse.getReportDownloadUrl().toString());
    }

    @Test
    void createReportResponse_ReturnsCorrectUrl() {

        ReportDetails mockReportDetails = ReportDetailsTestDataFactory.aValidReportResponse(
                VALID_REPORT_ID, "Test Report", "csv");

        when(reportManagementService.getDetailsForSpecificReport(VALID_REPORT_ID)).thenReturn(mockReportDetails);

        GetReportById200Response actualReportResponseDev = reportService.createReportResponse(VALID_REPORT_ID);

        assertTrue(actualReportResponseDev.getReportDownloadUrl().toString().contentEquals(ReportsTestDataFactory.TEST_DOWNLOAD_URL));

    }

}