package uk.gov.laa.gpfd.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
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
import uk.gov.laa.gpfd.model.Report;
import uk.gov.laa.gpfd.model.ReportsGet200ResponseReportListInner;
import uk.gov.laa.gpfd.services.ReportManagementService;
import uk.gov.laa.gpfd.services.ReportResponseBuilder;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.laa.gpfd.security.SilasRoles.FINANCIAL;
import static uk.gov.laa.gpfd.security.SilasRoles.REP000;
import static uk.gov.laa.gpfd.data.ReportsTestDataFactory.createTestReportWithOutputType;
import static uk.gov.laa.gpfd.data.ReportsTestDataFactory.csvReportOutput;
import static uk.gov.laa.gpfd.data.ReportsTestDataFactory.s3ReportOutput;
import static uk.gov.laa.gpfd.data.ReportsTestDataFactory.xlsxReportOutput;
import static uk.gov.laa.gpfd.exception.UnableToParseAuthDetailsException.AuthenticationIsNullException;

@WebMvcTest(ReportsController.class)
@Import(ReportsControllerTest.AsyncTestConfig.class)
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
    ReportResponseBuilder reportResponseBuilder;

    @MockitoBean
    TrackedStreamService trackedStreamService;

    @TestConfiguration
    static class AsyncTestConfig implements WebMvcConfigurer {
        @Override
        public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
            configurer.setTaskExecutor(new TaskExecutorAdapter(new SyncTaskExecutor()));
        }
    }

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

        when(streamingService.stream(report, FileExtension.CSV)).thenReturn(responseStream);
        when(reportDao.fetchReportById(reportId)).thenReturn(Optional.of(report));
        when(securityUtils.extractUserId()).thenReturn(USER_ID);
        when(trackedStreamService.wrapStream(any(), any(), any())).thenReturn(responseStream);
        when(reportResponseBuilder.buildResponse(any(), any(), any())).thenReturn(mockResponseEntity);

        var response = performAuthenticatedStreamingGet("/reports/" + reportId + "/csv", List.of(FINANCIAL));

        assertEquals(200, response.getResponse().getStatus());
        assertEquals("attachment; filename=data.csv", response.getResponse().getHeader(HttpHeaders.CONTENT_DISPOSITION));
        assertEquals(MediaType.APPLICATION_OCTET_STREAM_VALUE, response.getResponse().getContentType());

        assertEquals("1,John,Doe\n2,Jane,Smith\n", response.getResponse().getContentAsString());

        verify(reportDao, times(1)).fetchReportById(reportId);
        verify(reportDao, never()).verifyUserCanAccessReport(reportId);
        verify(streamingService).stream(report, FileExtension.CSV);
        verify(trackedStreamService).wrapStream(responseStream, reportId, USER_ID);
        verify(reportResponseBuilder).buildResponse(responseStream, "Test Report.csv", FileExtension.CSV);
        verify(reportManagementServiceMock).validateReportFormat(report, FileExtension.CSV);
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
        performAuthenticatedGet("/reports", List.of(FINANCIAL))
                .andExpect(status().isOk()).andExpect(jsonPath("$.reportList", hasSize(2)))
                .andExpect(jsonPath("$.reportList[0].id").value(String.valueOf(reportListEntryMock1.getId())))
                .andExpect(jsonPath("$.reportList[1].id").value(String.valueOf(reportListEntryMock2.getId())));

        verify(reportManagementServiceMock, times(1)).fetchReportListEntries();
    }

    @Test
    void getReportReturnsCorrectResponseEntity() throws Exception {

        GetReportById200Response reportResponseMock = new ReportResponseTestBuilder().withId(REPORT_ID).createReportResponse();

        // Mock the service
        when(reportManagementServiceMock.createReportResponse(REPORT_ID)).thenReturn(reportResponseMock);

        // Perform request and assert results
        performAuthenticatedGet("/reports/" + REPORT_ID, List.of(FINANCIAL))
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
        when(reportResponseBuilder.buildResponse(any(), any(), any(), any())).thenReturn(ResponseEntity.ok().body(responseStream));

        var result = performAuthenticatedStreamingGet("/reports/" + reportId + "/file", List.of(FINANCIAL));

        assertEquals(200, result.getResponse().getStatus());

        assertEquals("output!", result.getResponse().getContentAsString());

        verify(reportDao, times(1)).fetchReportById(reportId);
        verify(reportDao, never()).verifyUserCanAccessReport(reportId);
        verify(reportManagementServiceMock).validateReportFormat(report, FileExtension.S3STORAGE);
        verify(fileDownloadService, times(1)).getFileStreamResponse(reportId);
        verify(trackedStreamService, times(1)).wrapStream(any(StreamingResponseBody.class), eq(reportId), eq(USER_ID));
        verify(reportResponseBuilder, times(1)).buildResponse(responseStream, "file.csv", FileExtension.S3STORAGE, 120L);
    }

    @Test
    void getReportDownloadByIdReturnsErrorWhenIdInvalid() throws Exception {
        var reportId = "not a uuid";

        performAuthenticatedGet("/reports/" + reportId + "/file", List.of(FINANCIAL))
                .andExpect(status().isBadRequest()).andReturn();
    }

    @ParameterizedTest(name = "Rejects invalid filetype {0} for Excel download")
    @CsvSource({
            "S3STORAGE",
            "CSV"
    })

    void downloadExcelRejectsInvalidFiletypes(String actualFormat) throws Exception {

        var report = createReportWithOutputType(actualFormat);
        var uuid = report.getId();
        when(reportDao.fetchReportById(uuid)).thenReturn(Optional.of(report));

        doThrow(new InvalidReportFormatException(uuid, "XLSX", actualFormat))
                .when(reportManagementServiceMock)
                .validateReportFormat(report, FileExtension.XLSX);

        performAuthenticatedGet("/reports/" + uuid + "/excel", List.of(FINANCIAL))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(
                        "Report " + uuid +
                                " is not valid for XLSX retrieval. This report is in " +
                                actualFormat + " format."));

        verify(reportManagementServiceMock)
                .validateReportFormat(report, FileExtension.XLSX);

        verify(streamingService, never())
                .stream(report, FileExtension.XLSX);
    }

    @ParameterizedTest(name = "Rejects invalid filetype {0} for CSV download")
    @CsvSource({
            "XLSX",
            "S3STORAGE"
    })
    void downloadCsvRejectsInvalidFiletypes(String actualFormat) throws Exception {

        var report = createReportWithOutputType(actualFormat);
        var uuid = report.getId();
        when(reportDao.fetchReportById(uuid)).thenReturn(Optional.of(report));

        doThrow(new InvalidReportFormatException(uuid, "CSV", actualFormat))
                .when(reportManagementServiceMock)
                .validateReportFormat(report, FileExtension.CSV);

        performAuthenticatedGet("/reports/" + uuid + "/csv", List.of(FINANCIAL))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(
                        "Report " + uuid +
                                " is not valid for CSV retrieval. This report is in " +
                                actualFormat + " format."));

        verify(reportManagementServiceMock)
                .validateReportFormat(report, FileExtension.CSV);

        verify(streamingService, never())
                .stream(report, FileExtension.CSV);
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

        when(streamingService.stream(report, FileExtension.XLSX)).thenReturn(responseBody);
        when(securityUtils.extractUserId()).thenReturn(USER_ID);
        when(trackedStreamService.wrapStream(any(), any(), any())).thenReturn(responseBody);
        when(reportResponseBuilder.buildResponse(any(), any(), any())).thenReturn(mockResponseEntity);
        when(reportDao.fetchReportById(excelReportId)).thenReturn(Optional.of(report));

        // Perform the GET request
        var result = performAuthenticatedStreamingGet("/reports/"+ excelReportId + "/excel", List.of(FINANCIAL));

        assertEquals(200, result.getResponse().getStatus());

        assertEquals("attachment; filename=report.xlsx", result.getResponse().getHeader(HttpHeaders.CONTENT_DISPOSITION));

        verify(reportDao, times(1)).fetchReportById(excelReportId);
        verify(reportDao, never()).verifyUserCanAccessReport(excelReportId);
        verify(reportManagementServiceMock).validateReportFormat(report, FileExtension.XLSX);
        verify(streamingService).stream(report, FileExtension.XLSX);
        verify(trackedStreamService).wrapStream(responseBody, excelReportId, USER_ID);
        verify(reportResponseBuilder).buildResponse(responseBody, "Test Report.xlsx", FileExtension.XLSX);
    }

    @ParameterizedTest(name = "Rejects invalid filetype {1} for S3STORAGE download")
    @CsvSource({
            "f46b4d3d-c100-429a-bf9a-6c3305dbdbfa, CSV",
            "0d4da9ec-b0b3-4371-af10-f375330d85d1, XLSX"
    })
    void getReportDownloadByIdRejectsInvalidFiletypes(String reportId, String actualFormat) throws Exception {

        UUID uuid = UUID.fromString(reportId);
        var report = createReportWithOutputType(actualFormat);

        when(reportDao.fetchReportById(uuid)).thenReturn(Optional.of(report));
        doThrow(new InvalidReportFormatException(uuid, "S3STORAGE", actualFormat))
                .when(reportManagementServiceMock)
                .validateReportFormat(report, FileExtension.S3STORAGE);

        performAuthenticatedGet("/reports/" + uuid + "/file", List.of(FINANCIAL))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(
                        "Report " + uuid +
                                " is not valid for S3STORAGE retrieval. This report is in " +
                                actualFormat + " format."));

        verify(reportManagementServiceMock)
                .validateReportFormat(report, FileExtension.S3STORAGE);

        verify(fileDownloadService, never())
                .getFileStreamResponse(uuid);
    }

    @Test
    void csvIdGet_shouldReturn403_whenAccessDenied() throws Exception {
        doThrow(new ReportAccessException(REPORT_ID))
                .when(reportDao).fetchReportById(REPORT_ID);
        performAuthenticatedGet("/reports/" + REPORT_ID + "/csv", List.of(FINANCIAL))
                .andExpect(status().isForbidden());
    }

    @Test
    void excelIdGet_shouldReturn403_whenAccessDenied() throws Exception {
        doThrow(new ReportAccessException(REPORT_ID))
                .when(reportDao).fetchReportById(REPORT_ID);
        performAuthenticatedGet("/reports/" + REPORT_ID + "/excel", List.of(FINANCIAL))
                .andExpect(status().isForbidden());
    }

    @Test
    void downloadCsvFailsIfFailToGetUserId() throws Exception {
        var report = createTestReportWithOutputType(csvReportOutput);
        var reportId = report.getId();
        StreamingResponseBody responseStream = outputStream -> outputStream.write("csv".getBytes());

        when(reportDao.fetchReportById(reportId)).thenReturn(Optional.of(report));
        when(streamingService.stream(report, FileExtension.CSV)).thenReturn(responseStream);
        when(securityUtils.extractUserId()).thenThrow(new AuthenticationIsNullException());

        performAuthenticatedGet("/reports/" + reportId + "/csv", List.of(FINANCIAL))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void downloadExcelFailsIfFailToGetUserId() throws Exception {
        var report = createTestReportWithOutputType(xlsxReportOutput);
        var reportId = report.getId();
        StreamingResponseBody responseStream = outputStream -> outputStream.write("excel".getBytes());

        when(reportDao.fetchReportById(reportId)).thenReturn(Optional.of(report));
        when(streamingService.stream(report, FileExtension.XLSX)).thenReturn(responseStream);
        when(securityUtils.extractUserId()).thenThrow(new AuthenticationIsNullException());

        performAuthenticatedGet("/reports/" + reportId + "/excel", List.of(FINANCIAL))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void downloadFromS3FailsIfFailToGetUserId() throws Exception {
                var report = createTestReportWithOutputType(REPORT_ID, s3ReportOutput);
                var s3CsvDownload = mock(S3ClientWrapper.S3CsvDownload.class);

                when(reportDao.fetchReportById(REPORT_ID)).thenReturn(Optional.of(report));
                when(fileDownloadService.getFileStreamResponse(REPORT_ID)).thenReturn(s3CsvDownload);
        when(securityUtils.extractUserId()).thenThrow(new AuthenticationIsNullException());

        // Perform the GET request
        performAuthenticatedGet("/reports/" + REPORT_ID + "/file", List.of(REP000))
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

        when(reportDao.fetchReportById(reportId)).thenReturn(Optional.empty());
        when(securityUtils.extractUserId()).thenReturn(USER_ID);

        performAuthenticatedGet("/reports/" + reportId + "/csv", List.of(FINANCIAL))
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

        when(securityUtils.extractUserId()).thenReturn(USER_ID);
        when(reportDao.fetchReportById(excelReportId)).thenReturn(Optional.empty());

        // Perform the GET request
        performAuthenticatedGet("/reports/" + excelReportId + "/excel", List.of(FINANCIAL))
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

        performAuthenticatedGet("/reports/" + reportId + "/file", List.of(FINANCIAL))
                .andExpect(status().isNotFound());
    }

    private MvcResult performAuthenticatedStreamingGet(String uri, List<String> roles) throws Exception {
        var result = performAuthenticatedGet(uri, roles)
                .andReturn();

        if (result.getRequest().isAsyncStarted()) {
            var asyncResult = result.getAsyncResult();
            if (asyncResult instanceof Throwable throwable) {
                throw new IllegalStateException("Async streaming request failed", throwable);
            }
        }

        return result;
    }

        private static Report createReportWithOutputType(String outputType) {
                return switch (outputType) {
                        case "CSV" -> createTestReportWithOutputType(csvReportOutput);
                        case "XLSX" -> createTestReportWithOutputType(xlsxReportOutput);
                        case "S3STORAGE" -> createTestReportWithOutputType(s3ReportOutput);
                        default -> throw new IllegalArgumentException("Unsupported output type: " + outputType);
                };
        }

}