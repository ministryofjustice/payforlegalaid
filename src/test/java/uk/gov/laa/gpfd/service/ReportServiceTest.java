package uk.gov.laa.gpfd.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.laa.gpfd.builders.ReportResponseTestBuilder;
import uk.gov.laa.gpfd.config.AppConfig;
import uk.gov.laa.gpfd.dao.ReportViewsDao;
import uk.gov.laa.gpfd.data.ReportListEntryTestDataFactory;
import uk.gov.laa.gpfd.model.GetReportById200Response;
import uk.gov.laa.gpfd.model.ReportsGet200ResponseReportListInner;
import uk.gov.laa.gpfd.services.MappingTableService;
import uk.gov.laa.gpfd.services.ReportService;

import java.io.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {
    @Mock
    AppConfig appConfig;

    @Mock
    ReportViewsDao reportViewsDAO;

    @Mock
    MappingTableService mappingTableService;

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
    void createReportResponse_reportResponseMatchesValuesFromMappingTable() throws IOException {

        when(appConfig.getServiceUrl()).thenReturn("http://localhost");

        // Mocking the data from mapping table
        ReportsGet200ResponseReportListInner mockReportListResponse = ReportListEntryTestDataFactory.aValidReportsGet200ResponseReportListInner();

        GetReportById200Response expectedReportResponse = new ReportResponseTestBuilder().createReportResponse();
        when(mappingTableService.getDetailsForSpecificReport(1)).thenReturn(mockReportListResponse);

        //Act
        GetReportById200Response actualReportResponse = reportService.createReportResponse(1);

        //check something
        assertEquals(expectedReportResponse.getReportName(), actualReportResponse.getReportName());
        assertEquals(expectedReportResponse.getId(), actualReportResponse.getId());

    }


    @Test
    void createCSVStreamReturnsCorrectContent() throws Exception {
        // Arrange
        String sqlQuery = "SELECT * FROM exampleTable";
        List<Map<String, Object>> mockResultList = Arrays.asList(
                new LinkedHashMap<String, Object>() {{
                    put("id", 1);
                    put("DATE_AUTHORISED_CIS", Timestamp.valueOf(LocalDateTime.of(2023, 8, 7, 0, 0)));
                    put("name", "Example Report 1");
                }},
                new LinkedHashMap<String, Object>() {{
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
    void createReportResponse_ReturnsCorrectUrl() throws IOException {

        when(appConfig.getServiceUrl()).thenReturn("http://localhost");

        ReportsGet200ResponseReportListInner mockReportListResponseDev = ReportListEntryTestDataFactory.aValidReportsGet200ResponseReportListInner();

        when(mappingTableService.getDetailsForSpecificReport(1)).thenReturn(mockReportListResponseDev);

        GetReportById200Response actualReportResponseDev = reportService.createReportResponse(1);

        assertTrue(actualReportResponseDev.getReportDownloadUrl().toString().contentEquals("http://localhost/csv/1"));

    }

}