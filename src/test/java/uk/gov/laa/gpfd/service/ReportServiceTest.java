package uk.gov.laa.gpfd.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.laa.gpfd.config.AppConfig;
import uk.gov.laa.gpfd.dao.ReportViewsDao;
import uk.gov.laa.gpfd.data.ReportDetailsTestDataFactory;
import uk.gov.laa.gpfd.exception.ReportOutputTypeNotFoundException;
import uk.gov.laa.gpfd.model.GetReportById200Response;
import uk.gov.laa.gpfd.model.ReportDetails;
import uk.gov.laa.gpfd.services.ReportManagementService;
import uk.gov.laa.gpfd.services.ReportService;

import java.io.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {
    private static final UUID VALID_REPORT_ID = UUID.fromString("0d4da9ec-b0b3-4371-af10-f375330d85d1");
    private static final UUID REPORT_ID_FOR_UNKNOWN_TYPE = UUID.fromString("00000000-0000-0000-0000-000000000000");

    @Mock
    AppConfig appConfig;

    @Mock
    ReportViewsDao reportViewsDAO;

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
    void givenValidId_whenCreateReportResponse_thenValidResponseIsReturned() {
        //Given
        when(appConfig.getServiceUrl()).thenReturn("http://localhost");
        ReportDetails mockReportDetails = ReportDetailsTestDataFactory.aValidReportResponse(
            VALID_REPORT_ID, "Test Report", "csv");
        when(reportManagementService.getDetailsForSpecificReport(VALID_REPORT_ID)).thenReturn(mockReportDetails);
        //When
        GetReportById200Response actualReportResponse = reportService.createReportResponse(
            VALID_REPORT_ID);
        //then
        assertEquals("Test Report", actualReportResponse.getReportName());
        assertEquals(VALID_REPORT_ID, actualReportResponse.getId());
        assertEquals("http://localhost/csv/0d4da9ec-b0b3-4371-af10-f375330d85d1", actualReportResponse.getReportDownloadUrl().toString());
    }

    @Test
    void givenIdForUnknownExtension_whenCreateReportResponse_thenInvalidUrlIsReturned() {

        //Given
        ReportDetails mockReportDetails = ReportDetailsTestDataFactory.aValidReportResponse(
            REPORT_ID_FOR_UNKNOWN_TYPE, "Report With Unknown Type", "mp4");
        when(reportManagementService.getDetailsForSpecificReport(REPORT_ID_FOR_UNKNOWN_TYPE)).thenReturn(mockReportDetails);

        //When & then
        assertThrows(ReportOutputTypeNotFoundException.class, () -> reportService.createReportResponse(REPORT_ID_FOR_UNKNOWN_TYPE));

    }


    @Test
    void createCSVStreamReturnsCorrectContent() throws Exception {
        // Arrange
        String sqlQuery = "SELECT * FROM exampleTable";
        List<Map<String, Object>> mockResultList = Arrays.asList(
            new LinkedHashMap<>() {{
              put("id", 1);
              put("DATE_AUTHORISED_CIS", Timestamp.valueOf(LocalDateTime.of(2023, 8, 7, 0, 0)));
              put("name", "Example Report 1");
            }},
            new LinkedHashMap<>() {{
              put("id", 2);
              put("DATE_AUTHORISED_CIS", Timestamp.valueOf(LocalDateTime.of(2023, 12, 31, 1, 50)));
              put("name", "Example Report 2");
            }}
        );

        when(reportViewsDAO.callDataBase(sqlQuery)).thenReturn(mockResultList);

        // Act
        ByteArrayOutputStream outputStream = reportService.createCsvStream(sqlQuery);

        // Assert
        assertNotNull(outputStream);
        String resultContent = outputStream.toString();
        assertTrue(resultContent.contains("id,DATE_AUTHORISED_CIS,name"));
        assertTrue(resultContent.contains("1,2023-08-07 00:00:00.0,Example Report 1"));
        assertTrue(resultContent.contains("2,2023-12-31 01:50:00.0,Example Report 2"));


        // Verify the interaction with the mock
        verify(reportViewsDAO).callDataBase(sqlQuery);
    }

    @Test
    void createReportResponse_ReturnsCorrectUrl() {

        when(appConfig.getServiceUrl()).thenReturn("http://localhost");

        ReportDetails mockReportDetails = ReportDetailsTestDataFactory.aValidReportResponse(
            VALID_REPORT_ID, "Test Report", "csv");

        when(reportManagementService.getDetailsForSpecificReport(VALID_REPORT_ID)).thenReturn(mockReportDetails);

        GetReportById200Response actualReportResponseDev = reportService.createReportResponse(
            VALID_REPORT_ID);

        assertTrue(actualReportResponseDev.getReportDownloadUrl().toString().contentEquals("http://localhost/csv/0d4da9ec-b0b3-4371-af10-f375330d85d1"));

    }

}