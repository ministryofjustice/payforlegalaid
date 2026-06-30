package uk.gov.laa.gpfd.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import uk.gov.laa.gpfd.builders.ReportResponseTestBuilder;
import uk.gov.laa.gpfd.dao.ReportDao;
import uk.gov.laa.gpfd.data.ReportListEntryTestDataFactory;
import uk.gov.laa.gpfd.data.ReportsTestDataFactory;
import uk.gov.laa.gpfd.exception.InvalidReportFormatException;
import uk.gov.laa.gpfd.exception.ReportAccessException;
import uk.gov.laa.gpfd.model.FileExtension;
import uk.gov.laa.gpfd.model.GetReportById200Response;
import uk.gov.laa.gpfd.model.ReportsGet200ResponseReportListInner;
import uk.gov.laa.gpfd.services.ReportManagementService;
import uk.gov.laa.gpfd.services.ResponseBuilder;
import uk.gov.laa.gpfd.services.StreamingService;
import uk.gov.laa.gpfd.services.s3.FileDownloadService;
import uk.gov.laa.gpfd.services.s3.S3ClientWrapper;
import uk.gov.laa.gpfd.services.stream.TrackedStreamService;
import uk.gov.laa.gpfd.utils.BaseMvcTest;
import uk.gov.laa.gpfd.utils.SecurityUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.laa.gpfd.data.ReportsTestDataFactory.createTestReportWithOutputType;
import static uk.gov.laa.gpfd.data.ReportsTestDataFactory.csvReportOutput;
import static uk.gov.laa.gpfd.data.ReportsTestDataFactory.s3ReportOutput;
import static uk.gov.laa.gpfd.data.ReportsTestDataFactory.xlsxReportOutput;
import static uk.gov.laa.gpfd.exception.UnableToParseAuthDetailsException.AuthenticationIsNullException;

@WebMvcTest(ReportsController.class)
class ReportsControllerTest extends BaseMvcTest {

    private static final UUID REPORT_ID = UUID.fromString("0d4da9ec-b0b3-4371-af10-f375330d85d1");
    private static final UUID USER_ID = UUID.fromString("5aee3d3d-15d3-41ba-9646-06429a183f68");

    @MockitoBean
    ReportManagementService reportManagementServiceMock;

    @MockitoBean
    StreamingService streamingService;

    @MockitoBean
    FileDownloadService fileDownloadService;

    @MockitoBean
    ReportDao reportDao;

    @MockitoBean
    SecurityUtils securityUtils;

    @MockitoBean
    ResponseBuilder responseBuilder;

    @MockitoBean
    TrackedStreamService trackedStreamService;

    @Test
    void downloadCsvReturnsCorrectResponse() throws Exception {
        // Mock CSV data
        ByteArrayOutputStream csvDataOutputStream = new ByteArrayOutputStream();
        csvDataOutputStream.write("1,John,Doe\n".getBytes());
        csvDataOutputStream.write("2,Jane,Smith\n".getBytes());

        var report = createTestReportWithOutputType(csvReportOutput);
        var reportId = report.getId();

        StreamingResponseBody responseStream = outputStream -> {
            csvDataOutputStream.writeTo(outputStream);
            outputStream.flush();
        };

        ResponseEntity<StreamingResponseBody> mockResponseEntity =
                ResponseEntity.ok()
                        .header("Content-Disposition", "attachment; filename=data.csv")
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(responseStream);

        doNothing().when(reportDao).verifyUserCanAccessReport(reportId);
        when(streamingService.stream(reportId, FileExtension.CSV)).thenReturn(responseStream);
        when(reportDao.fetchReportById(reportId)).thenReturn(Optional.of(report));
        when(securityUtils.extractUserId()).thenReturn(USER_ID);
        when(trackedStreamService.wrapStream(any(), any(), any())).thenReturn(responseStream);
        when(responseBuilder.buildResponse(any(), any(), any())).thenReturn(mockResponseEntity);

        var response = performAuthenticatedGet("/reports/" + reportId + "/csv", List.of("Financial"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=data.csv"))
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andReturn();

        assertEquals("1,John,Doe\n2,Jane,Smith\n", response.getResponse().getContentAsString());

        verify(streamingService).stream(reportId, FileExtension.CSV);
        verify(trackedStreamService).wrapStream(responseStream, reportId, USER_ID);
        verify(responseBuilder).buildResponse(responseStream, "Test Report.csv", FileExtension.CSV);
        verify(reportManagementServiceMock).validateReportFormat(reportId, FileExtension.CSV);
    }

    @Test
    void getReportListReturnsCorrectResponseEntity() throws Exception {
        //Create Mock Response objects
        ReportsGet200ResponseReportListInner reportListEntryMock1 = ReportListEntryTestDataFactory.aValidReportsGet200ResponseReportListInner();
        ReportsGet200ResponseReportListInner reportListEntryMock2 = ReportListEntryTestDataFactory.aValidReportsGet200ResponseReportListInnerWithCustomId(REPORT_ID);

        //Add mock response objects to a list
        List<ReportsGet200ResponseReportListInner> reportListResponseMockList = Arrays.asList(reportListEntryMock1, reportListEntryMock2);
        // Mock the Service call
        when(reportManagementServiceMock.fetchReportListEntries()).thenReturn(reportListResponseMockList);

        // Perform request and assert results
        performAuthenticatedGet("/reports", List.of("Financial"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.reportList", hasSize(2)))
                .andExpect(jsonPath("$.reportList[0].id").value(reportListEntryMock1.getId().toString()))
                .andExpect(jsonPath("$.reportList[1].id").value(reportListEntryMock2.getId().toString()));

        verify(reportManagementServiceMock, times(1)).fetchReportListEntries();
    }

    @Test
    void getReportReturnsCorrectResponseEntity() throws Exception {

        GetReportById200Response reportResponseMock = new ReportResponseTestBuilder().withId(REPORT_ID).createReportResponse();

        // Mock the service
        when(reportManagementServiceMock.createReportResponse(REPORT_ID)).thenReturn(reportResponseMock);

        // Perform request and assert results
        performAuthenticatedGet("/reports/" + REPORT_ID, List.of("Financial"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(REPORT_ID.toString()))
                .andExpect(jsonPath("$.reportName").value(reportResponseMock.getReportName()));

        verify(reportManagementServiceMock, times(1)).createReportResponse(REPORT_ID);
    }

    @Test
    void getReportDownloadByIdReturnsCorrectResponseEntity() throws Exception {
        var report = ReportsTestDataFactory.createTestReportWithOutputType(s3ReportOutput);
        var reportId = report.getId();
        var s3CsvDownload = mock(S3ClientWrapper.S3CsvDownload.class);
        var responseMetadata = GetObjectResponse.builder().contentLength(120L).build();
        var inputStream = new ByteArrayInputStream("test".getBytes());
        var outputStream = new ByteArrayOutputStream();
        outputStream.write("output!".getBytes());
        var mockS3Response = new ResponseInputStream<>(responseMetadata, inputStream);
        StreamingResponseBody responseStream = output -> {
            outputStream.writeTo(output);
            outputStream.flush();
        };

        when(fileDownloadService.getFileStreamResponse(reportId)).thenReturn(s3CsvDownload);
        when(reportDao.fetchReportById(reportId)).thenReturn(Optional.of(report));
        when(s3CsvDownload.stream()).thenReturn(mockS3Response);
        when(s3CsvDownload.getFileName()).thenReturn("file.csv");
        when(trackedStreamService.wrapStream(any(), any(), any())).thenReturn(responseStream);
        when(securityUtils.extractUserId()).thenReturn(USER_ID);
        when(responseBuilder.buildResponse(any(), any(), any(), any())).thenReturn(ResponseEntity.ok().body(responseStream));

        performAuthenticatedGet("/reports/" + reportId + "/file", List.of("Financial"))
                .andExpect(status().isOk())
                .andExpect(content().string("output!"));

        verify(reportManagementServiceMock).validateReportFormat(reportId, FileExtension.S3STORAGE);
        verify(fileDownloadService, times(1)).getFileStreamResponse(reportId);
        verify(trackedStreamService, times(1)).wrapStream(any(StreamingResponseBody.class), eq(reportId), eq(USER_ID));
        verify(responseBuilder, times(1)).buildResponse(responseStream, "file.csv", FileExtension.S3STORAGE, 120L);
    }

    @Test
    void getReportDownloadByIdReturnsErrorWhenIdInvalid() throws Exception {
        var reportId = "not a uuid";

        performAuthenticatedGet("/reports/" + reportId + "/file", List.of("Financial"))
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

        performAuthenticatedGet("/reports/" + uuid + "/excel", List.of("Financial"))
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

        performAuthenticatedGet("/reports/" + uuid + "/csv", List.of("Financial"))
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
    void downloadExcelSucceedsForExcelReport() throws Exception {
        var report = ReportsTestDataFactory.createTestReportWithOutputType(xlsxReportOutput);
        var excelReportId = report.getId();

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
        when(streamingService.stream(excelReportId, FileExtension.XLSX)).thenReturn(responseBody);
        when(securityUtils.extractUserId()).thenReturn(USER_ID);
        when(trackedStreamService.wrapStream(any(), any(), any())).thenReturn(responseBody);
        when(responseBuilder.buildResponse(any(), any(), any())).thenReturn(mockResponseEntity);
        when(reportDao.fetchReportById(excelReportId)).thenReturn(Optional.of(report));

        // Perform the GET request
        performAuthenticatedGet("/reports/"+ excelReportId + "/excel", List.of("Financial"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.xlsx"));

        verify(reportManagementServiceMock).validateReportFormat(excelReportId, FileExtension.XLSX);
        verify(streamingService).stream(excelReportId, FileExtension.XLSX);
        verify(trackedStreamService).wrapStream(responseBody, excelReportId, USER_ID);
        verify(responseBuilder).buildResponse(responseBody, "Test Report.xlsx", FileExtension.XLSX);
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

        performAuthenticatedGet("/reports/" + uuid + "/file", List.of("Financial"))
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
    void csvIdGet_shouldReturn403_whenAccessDenied() throws Exception {
        doThrow(new ReportAccessException(REPORT_ID))
                .when(reportDao).verifyUserCanAccessReport(REPORT_ID);
        performAuthenticatedGet("/reports/" + REPORT_ID + "/csv", List.of("Financial"))
                .andExpect(status().isForbidden());
    }

    @Test
    void excelIdGet_shouldReturn403_whenAccessDenied() throws Exception {
        doThrow(new ReportAccessException(REPORT_ID))
                .when(reportDao).verifyUserCanAccessReport(REPORT_ID);
        performAuthenticatedGet("/reports/" + REPORT_ID + "/excel", List.of("Financial"))
                .andExpect(status().isForbidden());
    }

    @Test
    void downloadCsvFailsIfFailToGetUserId() throws Exception {

        when(securityUtils.extractUserId()).thenThrow(new AuthenticationIsNullException());

        // Perform the GET request
        performAuthenticatedGet("/reports/" + REPORT_ID + "/csv", List.of("Financial"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void downloadExcelFailsIfFailToGetUserId() throws Exception {

        when(securityUtils.extractUserId()).thenThrow(new AuthenticationIsNullException());

        // Perform the GET request
        performAuthenticatedGet("/reports/" + REPORT_ID + "/excel", List.of("Financial"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void downloadFromS3FailsIfFailToGetUserId() throws Exception {

        when(securityUtils.extractUserId()).thenThrow(new AuthenticationIsNullException());

        // Perform the GET request
        performAuthenticatedGet("/reports/" + REPORT_ID + "/file", List.of("REP000"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void downloadCsvFailsIfFetchReportByIdFails() throws Exception {
        // Mock CSV data
        ByteArrayOutputStream csvDataOutputStream = new ByteArrayOutputStream();
        csvDataOutputStream.write("1,John,Doe\n".getBytes());
        csvDataOutputStream.write("2,Jane,Smith\n".getBytes());

        var report = createTestReportWithOutputType(csvReportOutput);
        var reportId = report.getId();

        StreamingResponseBody responseStream = outputStream -> {
            csvDataOutputStream.writeTo(outputStream);
            outputStream.flush();
        };

        ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=data.csv")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(responseStream);

        doNothing().when(reportDao).verifyUserCanAccessReport(reportId);
        when(streamingService.stream(reportId, FileExtension.CSV)).thenReturn(responseStream);
        when(reportDao.fetchReportById(reportId)).thenReturn(Optional.empty());
        when(securityUtils.extractUserId()).thenReturn(USER_ID);

        performAuthenticatedGet("/reports/" + reportId + "/csv", List.of("Financial"))
                .andExpect(status().isNotFound());

    }

    @Test
    void downloadExcelFailsIfFetchReportByIdFails() throws Exception {
        var report = ReportsTestDataFactory.createTestReportWithOutputType(xlsxReportOutput);
        var excelReportId = report.getId();

        // Mock Excel data
        ByteArrayOutputStream excelDataOutputStream = new ByteArrayOutputStream();
        excelDataOutputStream.write("mock-excel-data".getBytes());

        StreamingResponseBody responseBody = outputStream -> {
            excelDataOutputStream.writeTo(outputStream);
            outputStream.flush();
        };

        ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=report.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(responseBody);

        // Validation passes (no exception thrown)
        doNothing().when(reportManagementServiceMock).validateReportFormat(excelReportId, FileExtension.XLSX);
        when(streamingService.stream(excelReportId, FileExtension.XLSX)).thenReturn(responseBody);
        when(securityUtils.extractUserId()).thenReturn(USER_ID);
        when(reportDao.fetchReportById(excelReportId)).thenReturn(Optional.empty());

        // Perform the GET request
        performAuthenticatedGet("/reports/" + excelReportId + "/excel", List.of("Financial"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getReportDownloadByIdFailsIfFetchReportByIdFails() throws Exception {
        var report = ReportsTestDataFactory.createTestReportWithOutputType(s3ReportOutput);
        var reportId = report.getId();
        var s3CsvDownload = mock(S3ClientWrapper.S3CsvDownload.class);
        var responseMetadata = GetObjectResponse.builder().contentLength(120L).build();
        var inputStream = new ByteArrayInputStream("test".getBytes());
        var outputStream = new ByteArrayOutputStream();
        outputStream.write("output!".getBytes());
        var mockS3Response = new ResponseInputStream<>(responseMetadata, inputStream);

        when(fileDownloadService.getFileStreamResponse(reportId)).thenReturn(s3CsvDownload);
        when(reportDao.fetchReportById(reportId)).thenReturn(Optional.empty());
        when(s3CsvDownload.stream()).thenReturn(mockS3Response);
        when(s3CsvDownload.getFileName()).thenReturn("file.csv");
        when(securityUtils.extractUserId()).thenReturn(USER_ID);

        performAuthenticatedGet("/reports/" + reportId + "/file", List.of("Financial"))
                .andExpect(status().isNotFound());
    }

}