package uk.gov.laa.gpfd.controller;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import uk.gov.laa.gpfd.builders.ReportResponseTestBuilder;
import uk.gov.laa.gpfd.dao.ReportDao;
import uk.gov.laa.gpfd.data.ReportListEntryTestDataFactory;
import uk.gov.laa.gpfd.exception.InvalidReportFormatException;
import uk.gov.laa.gpfd.model.FileExtension;
import uk.gov.laa.gpfd.model.GetReportById200Response;
import uk.gov.laa.gpfd.model.ReportsGet200ResponseReportListInner;
import uk.gov.laa.gpfd.services.ReportManagementService;
import uk.gov.laa.gpfd.services.ReportsTrackingService;
import uk.gov.laa.gpfd.services.StreamingService;
import uk.gov.laa.gpfd.services.s3.FileDownloadService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Client;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT ,
        classes = uk.gov.laa.gpfd.config.TestDatabaseConfig .class)
@AutoConfigureMockMvc
@ActiveProfiles("testauth")
class ReportsControllerTest {

    public static final UUID DEFAULT_ID = UUID.fromString("0d4da9ec-b0b3-4371-af10-f375330d85d1");

    @MockitoBean
    ReportManagementService reportManagementServiceMock;

    @MockitoBean
    ReportsTrackingService reportsTrackingService; // This is required, despite the sonarlint suggestions

    @MockitoBean
    StreamingService streamingService;

    @MockitoBean
    FileDownloadService fileDownloadService;

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ReportDao reportDao;

    @Test
    void downloadCsvReturnsCorrectResponse() throws Exception {
        // Mock CSV data
        ByteArrayOutputStream csvDataOutputStream = new ByteArrayOutputStream();
        csvDataOutputStream.write("1,John,Doe\n".getBytes());
        csvDataOutputStream.write("2,Jane,Smith\n".getBytes());

        StreamingResponseBody responseBody = outputStream -> {
            csvDataOutputStream.writeTo(outputStream);
            outputStream.flush();
        };

        ResponseEntity<StreamingResponseBody> mockResponseEntity =
                ResponseEntity.ok()
                        .header("Content-Disposition", "attachment; filename=data.csv")
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(responseBody);

        when(streamingService.stream(DEFAULT_ID, FileExtension.CSV))
                .thenReturn(mockResponseEntity);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/csv/0d4da9ec-b0b3-4371-af10-f375330d85d1")
                                .with(oidcLogin()
                                        .idToken(token -> token.claim("LAA_APP_ROLES", List.of("REP000")))
                                ))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=data.csv"));

        verify(streamingService).stream(DEFAULT_ID, FileExtension.CSV);
    }


    @Test
    void getReportListReturnsCorrectResponseEntity() throws Exception {
        //Create Mock Response objects
        ReportsGet200ResponseReportListInner reportListEntryMock1 = ReportListEntryTestDataFactory.aValidReportsGet200ResponseReportListInner();
        ReportsGet200ResponseReportListInner reportListEntryMock2 = ReportListEntryTestDataFactory.aValidReportsGet200ResponseReportListInnerWithCustomId(DEFAULT_ID);

        //Add mock response objects to a list
        List<ReportsGet200ResponseReportListInner> reportListResponseMockList = Arrays.asList(reportListEntryMock1, reportListEntryMock2);
        // Mock the Service call
        when(reportManagementServiceMock.fetchReportListEntries()).thenReturn(reportListResponseMockList);

        // Perform request and assert results
        mockMvc.perform(MockMvcRequestBuilders.get("/reports")
                        .with(oidcLogin()
                                .idToken(token -> token.claim("LAA_APP_ROLES", List.of("Financial")))))
                .andExpect(status().isOk()).andExpect(jsonPath("$.reportList", hasSize(2)))
                .andExpect(jsonPath("$.reportList[0].id").value(reportListEntryMock1.getId().toString()))
                .andExpect(jsonPath("$.reportList[1].id").value(reportListEntryMock2.getId().toString()));

        verify(reportManagementServiceMock, times(1)).fetchReportListEntries();

    }

    @Test
    void getReportReturnsCorrectResponseEntity() throws Exception {
        var reportId = DEFAULT_ID;

        GetReportById200Response reportResponseMock = new ReportResponseTestBuilder().withId(reportId).createReportResponse();

        // Mock the service
        when(reportManagementServiceMock.createReportResponse(reportId)).thenReturn(reportResponseMock);

        // Perform request and assert results
        mockMvc.perform(MockMvcRequestBuilders.get("/reports/{id}", reportId)
                .with(oidcLogin()
                        .idToken(token -> token.claim("LAA_APP_ROLES", List.of("Financial"))))).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(reportId.toString())).andExpect(jsonPath("$.reportName").value(reportResponseMock.getReportName()));

        verify(reportManagementServiceMock, times(1)).createReportResponse(reportId);
    }

    @Test
    void getReportDownloadByIdReturnsCorrectResponseEntity() throws Exception {
        var reportId = DEFAULT_ID;

        var inputStreamResource = new InputStreamResource(new ByteArrayInputStream("test".getBytes()));
        var mockResponse = ResponseEntity.ok(inputStreamResource);

        when(fileDownloadService.getFileStreamResponse(reportId)).thenReturn(mockResponse);

        var result = mockMvc.perform(MockMvcRequestBuilders.get("/reports/{id}/file", reportId).with(oidcLogin()
                        .idToken(token -> token.claim("LAA_APP_ROLES", List.of("Financial")))))
                .andExpect(status().isOk()).andReturn();

        assertEquals("test", result.getResponse().getContentAsString());
        verify(fileDownloadService, times(1)).getFileStreamResponse(reportId);
    }

    @Test
    void getReportDownloadByIdReturnsErrorWhenIdInvalid() throws Exception {
        var reportId = "not a uuid";

        mockMvc.perform(MockMvcRequestBuilders.get("/reports/{id}/file", reportId).with(oidcLogin()
                        .idToken(token -> token.claim("LAA_APP_ROLES", List.of("Financial")))))
                .andExpect(status().isBadRequest()).andReturn();
    }

    @ParameterizedTest(name = "Rejects invalid filetype {1} for Excel download")
    @CsvSource({
            "523f38f0-2179-4824-b885-3a38c5e149e8, S3STORAGE",
            "f46b4d3d-c100-429a-bf9a-6c3305dbdbfa, CSV"
    })
    void downloadExcelRejectsInvalidFiletypes(String reportId, String actualFormat) throws Exception {

        UUID uuid = UUID.fromString(reportId);
        doNothing().when(reportDao).verifyUserCanAccessReport(uuid);

        doThrow(new InvalidReportFormatException(uuid, "XLSX", actualFormat))
                .when(reportManagementServiceMock)
                .validateReportFormat(uuid, FileExtension.XLSX);

        mockMvc.perform(MockMvcRequestBuilders.get("/excel/{id}", uuid)
                        .with(oidcLogin()
                                .idToken(token -> token.claim("LAA_APP_ROLES", List.of("REP000")))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(
                        "Report " + uuid +
                                " is not valid for XLSX retrieval. This report is in " +
                                actualFormat + " format."));

        verify(reportManagementServiceMock)
                .validateReportFormat(uuid, FileExtension.XLSX);

        verify(streamingService, never())
                .stream(uuid, FileExtension.XLSX);
    }

    @ParameterizedTest(name = "Rejects invalid filetype {1} for CSV download")
    @CsvSource({
            "0d4da9ec-b0b3-4371-af10-f375330d85d1, XLSX",
            "523f38f0-2179-4824-b885-3a38c5e149e8, S3STORAGE"
    })
    void downloadCsvRejectsInvalidFiletypes(String reportId, String actualFormat) throws Exception {

        UUID uuid = UUID.fromString(reportId);

        doThrow(new InvalidReportFormatException(uuid, "CSV", actualFormat))
                .when(reportManagementServiceMock)
                .validateReportFormat(uuid, FileExtension.CSV);

        mockMvc.perform(MockMvcRequestBuilders.get("/csv/{id}", uuid)
                        .with(oidcLogin())
                        .with(oauth2Client("graph")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(
                        "Report " + uuid +
                                " is not valid for CSV retrieval. This report is in " +
                                actualFormat + " format."));

        verify(reportManagementServiceMock)
                .validateReportFormat(uuid, FileExtension.CSV);

        verify(streamingService, never())
                .stream(uuid, FileExtension.CSV);
    }

    @Test
    void downloadCsvSucceedsForCsvReport() throws Exception {
        var csvReportId = DEFAULT_ID;

        // Mock CSV data
        ByteArrayOutputStream csvDataOutputStream = new ByteArrayOutputStream();
        csvDataOutputStream.write("1,John,Doe\n".getBytes());

        StreamingResponseBody responseBody = outputStream -> {
            csvDataOutputStream.writeTo(outputStream);
            outputStream.flush();
        };

        ResponseEntity<StreamingResponseBody> mockResponseEntity = ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=data.csv")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(responseBody);

        // Validation passes (no exception thrown)
        doNothing().when(reportManagementServiceMock).validateReportFormat(csvReportId, FileExtension.CSV);
        when(streamingService.stream(csvReportId, FileExtension.CSV)).thenReturn(mockResponseEntity);

        // Perform the GET request
        mockMvc.perform(MockMvcRequestBuilders.get("/csv/{id}", csvReportId)
                        .with(oidcLogin())
                        .with(oauth2Client("graph")))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=data.csv"));

        verify(reportManagementServiceMock).validateReportFormat(csvReportId, FileExtension.CSV);
        verify(streamingService).stream(csvReportId, FileExtension.CSV);
    }

    @Test
    void downloadExcelSucceedsForExcelReport() throws Exception {
        var excelReportId = DEFAULT_ID;

        // Mock Excel data
        ByteArrayOutputStream excelDataOutputStream = new ByteArrayOutputStream();
        excelDataOutputStream.write("mock-excel-data".getBytes());

        StreamingResponseBody responseBody = outputStream -> {
            excelDataOutputStream.writeTo(outputStream);
            outputStream.flush();
        };

        ResponseEntity<StreamingResponseBody> mockResponseEntity = ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=report.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(responseBody);

        // Validation passes (no exception thrown)
        doNothing().when(reportManagementServiceMock).validateReportFormat(excelReportId, FileExtension.XLSX);
        when(streamingService.stream(excelReportId, FileExtension.XLSX)).thenReturn(mockResponseEntity);

        // Perform the GET request
        mockMvc.perform(MockMvcRequestBuilders.get("/excel/{id}", excelReportId)
                        .with(oidcLogin())
                        .with(oauth2Client("graph")))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.xlsx"));

        verify(reportManagementServiceMock).validateReportFormat(excelReportId, FileExtension.XLSX);
        verify(streamingService).stream(excelReportId, FileExtension.XLSX);
    }

    @ParameterizedTest(name = "Rejects invalid filetype {1} for S3STORAGE download")
    @CsvSource({
            "f46b4d3d-c100-429a-bf9a-6c3305dbdbfa, CSV",
            "0d4da9ec-b0b3-4371-af10-f375330d85d1, XLSX"
    })
    void getReportDownloadByIdRejectsInvalidFiletypes(String reportId, String actualFormat) throws Exception {

        UUID uuid = UUID.fromString(reportId);

        doThrow(new InvalidReportFormatException(uuid, "S3STORAGE", actualFormat))
                .when(reportManagementServiceMock)
                .validateReportFormat(uuid, FileExtension.S3STORAGE);

        mockMvc.perform(MockMvcRequestBuilders.get("/reports/{id}/file", uuid).with(oidcLogin()
                        .idToken(token -> token.claim("LAA_APP_ROLES", List.of("REP000")))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(
                        "Report " + uuid +
                                " is not valid for S3STORAGE retrieval. This report is in " +
                                actualFormat + " format."));

        verify(reportManagementServiceMock)
                .validateReportFormat(uuid, FileExtension.S3STORAGE);

        verify(fileDownloadService, never())
                .getFileStreamResponse(uuid);
    }

    @Test
    void getReportDownloadByIdSucceedsForS3StorageReport() throws Exception {
        var s3ReportId = UUID.fromString("523f38f0-2179-4824-b885-3a38c5e149e8");

        var inputStreamResource = new InputStreamResource(new ByteArrayInputStream("test".getBytes()));
        var mockResponse = ResponseEntity.ok(inputStreamResource);

        // Validation passes (no exception thrown)
        doNothing().when(reportManagementServiceMock).validateReportFormat(s3ReportId, FileExtension.S3STORAGE);  // Updated this line
        when(fileDownloadService.getFileStreamResponse(s3ReportId)).thenReturn(mockResponse);

        var result = mockMvc.perform(MockMvcRequestBuilders.get("/reports/{id}/file", s3ReportId).with(oidcLogin()
                        .idToken(token -> token.claim("LAA_APP_ROLES", List.of("REP000")))))
                .andExpect(status().isOk()).andReturn();

        assertEquals("test", result.getResponse().getContentAsString());
        verify(reportManagementServiceMock).validateReportFormat(s3ReportId, FileExtension.S3STORAGE);  // Updated this line
        verify(fileDownloadService, times(1)).getFileStreamResponse(s3ReportId);
    }

}