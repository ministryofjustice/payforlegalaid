package uk.gov.laa.gpfd.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import uk.gov.laa.gpfd.dao.ReportDao;
import uk.gov.laa.gpfd.data.ReportsTestDataFactory;
import uk.gov.laa.gpfd.model.FileExtension;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static uk.gov.laa.gpfd.data.ReportsTestDataFactory.s3ReportOutput;

@WebMvcTest(ReportsController.class)
@Import(ReportsControllerFeatureFlagOffTest.AsyncTestConfig.class)
class ReportsControllerFeatureFlagOffTest extends BaseMvcTest {

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
    void shouldUseCurrentS3ImplementationWhenSdsBeanIsNotPresent() throws Exception {
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
            output.flush();
        };

        doNothing().when(reportDao).verifyUserCanAccessReport(reportId);
        when(fileDownloadService.getFileStreamResponse(reportId)).thenReturn(s3CsvDownload);
        when(reportDao.fetchReportById(reportId)).thenReturn(Optional.of(report));
        when(s3CsvDownload.stream()).thenReturn(mockS3Response);
        when(s3CsvDownload.getFileName()).thenReturn("file.csv");
        when(trackedStreamService.wrapStream(any(), any(), any())).thenReturn(responseStream);
        when(securityUtils.extractUserId()).thenReturn(USER_ID);
        when(reportResponseBuilder.buildResponse(any(), any(), any(), any())).thenReturn(ResponseEntity.ok().body(responseStream));

        var result = performAuthenticatedStreamingGet("/reports/" + reportId + "/file", List.of("Financial"));

        assertEquals(200, result.getResponse().getStatus());
        assertEquals("output!", result.getResponse().getContentAsString());

        verify(reportManagementServiceMock).validateReportFormat(reportId, FileExtension.S3STORAGE);
        verify(fileDownloadService, times(1)).getFileStreamResponse(reportId);
        verify(trackedStreamService, times(1)).wrapStream(any(StreamingResponseBody.class), eq(reportId), eq(USER_ID));
        verify(reportResponseBuilder, times(1)).buildResponse(responseStream, "file.csv", FileExtension.S3STORAGE, 120L);
    }

    private MvcResult performAuthenticatedStreamingGet(String uri, List<String> roles) throws Exception {
        var result = performAuthenticatedGet(uri, roles).andReturn();
        if (result.getRequest().isAsyncStarted()) {
            return mockMvc.perform(asyncDispatch(result)).andReturn();
        }
        return result;
    }
}
