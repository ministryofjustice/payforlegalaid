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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {
    private static final UUID id = UUID.fromString("0d4da9ec-b0b3-4371-af10-f375330d85d1");

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
        when(mappingTableService.getDetailsForSpecificReport(id)).thenReturn(mockReportListResponse);

        //Act
        GetReportById200Response actualReportResponse = reportService.createReportResponse(id);

        //check something
        assertEquals(expectedReportResponse.getReportName(), actualReportResponse.getReportName());
        assertEquals(expectedReportResponse.getId(), actualReportResponse.getId());

    }

    @Test
    void createReportResponse_ReturnsCorrectUrl() throws IOException {

        when(appConfig.getServiceUrl()).thenReturn("http://localhost");

        ReportsGet200ResponseReportListInner mockReportListResponseDev = ReportListEntryTestDataFactory.aValidReportsGet200ResponseReportListInner();

        when(mappingTableService.getDetailsForSpecificReport(id)).thenReturn(mockReportListResponseDev);

        GetReportById200Response actualReportResponseDev = reportService.createReportResponse(id);

        assertTrue(actualReportResponseDev.getReportDownloadUrl().toString().contentEquals("http://localhost/csv/0d4da9ec-b0b3-4371-af10-f375330d85d1"));

    }

}