package uk.gov.laa.pfla.auth.service.service;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
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
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.laa.pfla.auth.service.services.SharePointService;

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
    SharePointService sharePointService;

    @Mock
    MappingTableService mappingTableService;

    @Mock
    RestTemplate restTemplate;

    @Mock
    OAuth2AuthorizedClient mockAuthorizedClient;
    @InjectMocks
    ReportService reportService;
    List<Map<String, Object>> reportMapMockList = new ArrayList<>();

    @Captor
    private ArgumentCaptor<InputStream> inputStreamCaptor;


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

//        MockitoAnnotations.initMocks(this);

    }

//    @Test
//    void fetchReportViewObjectList_ReturnsCorrectReportModelType(){
//
//        // Arrange
//        String sqlQuery = "SELECT * FROM ANY_REPORT.V_BANK_MONTH";
//
//        VBankMonth expectedReportViewObject = VBankMonth.builder()
//                .source("CCMS")
//                .invSource("CCMS")
//                .subSource("Applied Receipts")
//                .paymentDate("05-jul-23")
//                .paymentMonth("31-jul-23")
//                .settlementType("Applied Receipts")
//                .scheme("Civil")
//                .subScheme("Civil Representation")
//                .detailDesc("Costs Interest")
//                .catCode("N/A")
//                .apArMovement("N")
//                .total(-15)
//                .build();
//
//        final List<ReportModel> expectedReportViewObjectList = new ArrayList<>();
//        expectedReportViewObjectList.add(expectedReportViewObject);
//
//        // Simulate report model and service behavior
//        when(reportViewsDAO.fetchReportViewObjectList(sqlQuery,VBankMonth.class)).thenReturn(expectedReportViewObjectList);
//        // Act
//        List<ReportModel> actualReportViewObjectList = reportService.fetchReportViewObjectList(VBankMonth.class, sqlQuery);
//
//        // Assert
//        verify(reportViewsDAO, times(1)).fetchReportViewObjectList(sqlQuery,VBankMonth.class);
//        assertEquals(1, actualReportViewObjectList.size());
//        assertEquals(expectedReportViewObject, actualReportViewObjectList.get(0));
//        assertThat(actualReportViewObjectList.get(0), instanceOf(VBankMonth.class));
//
//    }


//    @Test
//    void testConvertToCSVandWriteToFile() throws IOException {
//        // Arrange
//        String fileName = reportService.convertToCSVandWriteToFile(reportMapMockList, VCisToCcmsInvoiceSummaryModel.class);
//
//        File file = new File(fileName);
//        String fileContent = null;
//        try {
//            fileContent = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
//        } catch (IOException e) {
//            log.info("Error reading file: " + e);
//        }
//        String expectedContent = "name,balance,system\r\n" +
//                "CCMS Report,12300,ccms\r\n" +
//                "CCMS Report2,16300,ccms\r\n";
//
//        // Assert
//        Assertions.assertTrue(file.exists(), "file should be created");
//        Assertions.assertEquals(expectedContent, fileContent, "File content does not match");
//        assertTrue(reportService.deleteLocalFile(fileName));
//    }
//
//    @Test
//    void testConvertToCSVandWriteToFileWithExceptionScenario() {
//        // Arrange
//        List<Map<String, Object>> emptyList = List.of();
//
//        // Assert that an ArrayIndexOutOfBoundsException is thrown if the method is given an empty list
//        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
//            reportService.convertToCSVandWriteToFile(emptyList, VBankMonth.class);
//        }, "Expected exception was not thrown");
//
//    }




    @Test
    void createReportResponse_reportResponseMatchesValuesFromMappingTable() throws IOException {

        // Mocking the data from mapping table
        ReportListResponse mockReportListResponse =  new ReportListResponseTestBuilder().createReportListResponse();

        ReportResponse expectedReportResponse = new ReportResponseTestBuilder().createReportResponse();


        when(mappingTableService.getDetailsForSpecificReport(1)).thenReturn(mockReportListResponse);
//        when(reportViewsDAO.callDataBase(mockReportListResponse.getSqlQuery())).thenReturn(reportMapMockList);


        ReportResponse actualReportResponse = reportService.createReportResponse(1, mockAuthorizedClient);

        //check something
        assertEquals(expectedReportResponse.getReportName(), actualReportResponse.getReportName());
        assertEquals(expectedReportResponse.getId(), actualReportResponse.getId());
        assertEquals(expectedReportResponse.getReportUrl(), actualReportResponse.getReportUrl());

    }


//
//    @Test
//    void testGenerateAndUploadCsvToSharePoint() throws IOException {
//
//        // Mock the RestTemplate behavior
//        String sharePointApiUrl = "https://placeholder-sharepoint-site/api/upload"; //This needs to match the real URI
//        HttpHeaders headers = new HttpHeaders();
//        ResponseEntity<Void> responseEntity = new ResponseEntity<>(headers, HttpStatus.CREATED);
//        when(restTemplate.postForLocation(eq(sharePointApiUrl), inputStreamCaptor.capture()))
//                .thenReturn(null);
//
//        // Act
//        reportService.generateAndUploadCsvToSharePoint(reportMapMockList);
//
//
//        // Extract the content of the captured InputStream (captured from the postForLocation() method call)
//        InputStream capturedInputStream = inputStreamCaptor.getValue();
//        byte[] expectedCsvBytes = "name,balance,system\r\nCCMS Report,12300,ccms\r\nCCMS Report2,16300,ccms\r\n".getBytes();
//        byte[] capturedCsvBytes = IOUtils.toByteArray(capturedInputStream);
//
//
//        // Convert byte arrays to strings for debugging
//        String expectedCsvString = new String(expectedCsvBytes, StandardCharsets.UTF_8);
//        String actualCsvString = new String(capturedCsvBytes, StandardCharsets.UTF_8);
//        System.out.println("Expected CSV:\r\n" + expectedCsvString);
//        System.out.println("Actual CSV:\r\n" + actualCsvString);
//
//
//        // Assert on the content of the CSV strings
//        assertEquals(expectedCsvString, actualCsvString);
//        assertArrayEquals(expectedCsvBytes, capturedCsvBytes);
//
//        // Verify that RestTemplate's postForEntity was called with the correct arguments
//        verify(restTemplate, times(1)).postForLocation(eq(sharePointApiUrl), inputStreamCaptor.capture());
//        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
//        assertTrue(responseEntity.getHeaders().isEmpty());
//
//    }

    @Test
    void createCSVStreamReturnsCorrectContent() throws Exception {
        // Arrange
        String sqlQuery = "SELECT * FROM exampleTable";
        List<Map<String, Object>> mockResultList = Arrays.asList(
                new LinkedHashMap<String, Object>() {{
                    put("id", 1);
                    put("DATE_AUTHORISED_CIS", Timestamp.valueOf(LocalDateTime.of(2023, 8, 07, 0, 0)));
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
//    @Test
//    public void testCreateCsvStream() throws IOException {
//
//
//        List<Map<String, Object>> resultList = [{DATE_AUTHORISED_CIS=2023-08-07 00:00:00.0, THE_SYSTEM=CWA Crime Lower Contract, CIS_VALUE=10466.08, CCMS_VALUE=10466.08}, {DATE_AUTHORISED_CIS=2023-08-07 00:00:00.0, THE_SYSTEM=AGFS scheme, CIS_VALUE=1258037.63, CCMS_VALUE=1256489.69}, {DATE_AUTHORISED_CIS=2023-08-08 00:00:00.0, THE_SYSTEM=eForms, CIS_VALUE=8502.7, CCMS_VALUE=8502.7}]
//
//        // Mock CSV data
//        ByteArrayOutputStream csvDataOutputStream = new ByteArrayOutputStream();
//        csvDataOutputStream.write("1,John,Doe\n".getBytes());
//        csvDataOutputStream.write("2,Jane,Smith\n".getBytes());
//
//        // Mock response body
//        StreamingResponseBody responseBody = outputStream -> {
//            csvDataOutputStream.writeTo(outputStream);
//            outputStream.flush();
//        };
//
//        // Mock ResponseEntity
//        ResponseEntity<StreamingResponseBody> mockResponseEntity = ResponseEntity.ok()
//                .header("Content-Disposition", "attachment; filename=data.csv")
//                .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                .body(responseBody);
//
//        // Mock CSVController behavior
////        when(reportsController.getCSV(1)).thenReturn(mockResponseEntity);
//        when(reportService.createCsvStream(1, reportListResponse.getSqlQuery())).thenReturn(reportResponseMock);
//
//
//
//        // Verify response status
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//
//        // Verify content type
//        assertEquals(MediaType.APPLICATION_OCTET_STREAM, responseEntity.getHeaders().getContentType());
//
//        // Verify content disposition header
//        assertEquals("attachment; filename=data.csv", responseEntity.getHeaders().getFirst("Content-Disposition"));
//
//        // Verify CSV data
//        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
//        responseEntity.getBody().writeTo(mockHttpServletResponse.getOutputStream());
//        assertEquals("1,John,Doe\n2,Jane,Smith\n", mockHttpServletResponse.getContentAsString());
//    }

}