package uk.gov.laa.pfla.auth.service.service;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.laa.pfla.auth.service.builders.ReportListResponseTestBuilder;
import uk.gov.laa.pfla.auth.service.builders.ReportResponseTestBuilder;
import uk.gov.laa.pfla.auth.service.dao.ReportViewsDao;
import uk.gov.laa.pfla.auth.service.models.report_view_models.ReportModel;
import uk.gov.laa.pfla.auth.service.models.report_view_models.VBankMonth;
import uk.gov.laa.pfla.auth.service.models.report_view_models.VCisToCcmsInvoiceSummaryModel;
import uk.gov.laa.pfla.auth.service.responses.ReportListResponse;
import uk.gov.laa.pfla.auth.service.responses.ReportResponse;
import uk.gov.laa.pfla.auth.service.services.MappingTableService;
import uk.gov.laa.pfla.auth.service.services.ReportService;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {
    public static final Logger log = LoggerFactory.getLogger(ReportServiceTest.class);

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
    void fetchReportViewObjectList_ReturnsCorrectReportModelType(){

        // Arrange
        String sqlQuery = "SELECT * FROM ANY_REPORT.V_BANK_MONTH";

        VBankMonth expectedReportViewObject = VBankMonth.builder()
                .source("CCMS")
                .invSource("CCMS")
                .subSource("Applied Receipts")
                .paymentDate("05-jul-23")
                .paymentMonth("31-jul-23")
                .settlementType("Applied Receipts")
                .scheme("Civil")
                .subScheme("Civil Representation")
                .detailDesc("Costs Interest")
                .catCode("N/A")
                .apArMovement("N")
                .total(-15)
                .build();

        final List<ReportModel> expectedReportViewObjectList = new ArrayList<>();
        expectedReportViewObjectList.add(expectedReportViewObject);

        // Simulate report model and service behavior
        when(reportViewsDAO.fetchReportViewObjectList(sqlQuery,VBankMonth.class)).thenReturn(expectedReportViewObjectList);
        // Act
        List<ReportModel> actualReportViewObjectList = reportService.fetchReportViewObjectList(VBankMonth.class, sqlQuery);

        // Assert
        verify(reportViewsDAO, times(1)).fetchReportViewObjectList(sqlQuery,VBankMonth.class);
        assertEquals(1, actualReportViewObjectList.size());
        assertEquals(expectedReportViewObject, actualReportViewObjectList.get(0));
        assertThat(actualReportViewObjectList.get(0), instanceOf(VBankMonth.class));

    }


    @Test
    void testConvertToCSVandWriteToFile() throws IOException {
        // Arrange
        String fileName = reportService.convertToCSVandWriteToFile(reportMapMockList, VCisToCcmsInvoiceSummaryModel.class);

        File file = new File(fileName);
        String fileContent = null;
        try {
            fileContent = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.info("Error reading file: " + e);
        }
        String expectedContent = "name,balance,system\r\n" +
                "CCMS Report,12300,ccms\r\n" +
                "CCMS Report2,16300,ccms\r\n";
        
        // Assert
        Assertions.assertTrue(file.exists(), "file should be created");
        Assertions.assertEquals(expectedContent, fileContent, "File content does not match");
        assertTrue(reportService.deleteLocalFile(fileName));
    }

    @Test
    void testConvertToCSVandWriteToFileWithExceptionScenario() {
        // Arrange
        List<Map<String, Object>> emptyList = List.of();

        // Assert that an ArrayIndexOutOfBoundsException is thrown if the method is given an empty list
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            reportService.convertToCSVandWriteToFile(emptyList, VBankMonth.class);
        }, "Expected exception was not thrown");

    }




    @Test
    void createReportResponse_reportResponseMatchesValuesFromMappingTable() throws IOException {

        // Mocking the data from mapping table
        ReportListResponse mockReportListResponse =  new ReportListResponseTestBuilder().createReportListResponse();

        ReportResponse expectedReportResponse = new ReportResponseTestBuilder().createReportResponse();


        when(mappingTableService.getDetailsForSpecificReport(1)).thenReturn(mockReportListResponse);
        when(reportViewsDAO.callDataBase(mockReportListResponse.getSqlQuery())).thenReturn(reportMapMockList);


        ReportResponse actualReportResponse = reportService.createReportResponse(1);

        //check something
        assertEquals(expectedReportResponse.getReportName(), actualReportResponse.getReportName());
        assertEquals(expectedReportResponse.getId(), actualReportResponse.getId());
        assertEquals(expectedReportResponse.getReportUrl(), actualReportResponse.getReportUrl());

    }

}