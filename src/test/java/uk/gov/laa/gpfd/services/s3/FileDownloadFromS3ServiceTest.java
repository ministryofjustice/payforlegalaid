package uk.gov.laa.gpfd.services.s3;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import uk.gov.laa.gpfd.dao.ReportDao;
import uk.gov.laa.gpfd.exception.FileDownloadException.S3BucketHasNoCopiesOfReportException;
import uk.gov.laa.gpfd.exception.ReportAccessException;
import uk.gov.laa.gpfd.services.s3.S3ClientWrapper.S3CsvDownload;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileDownloadFromS3ServiceTest {

    @Mock
    private S3ClientWrapper s3ClientWrapper;

    @Mock
    private ReportFileNameResolver fileNameResolver;

    @InjectMocks
    private FileDownloadFromS3Service fileDownloadFromS3Service;

    @Mock
    private ReportDao reportDao;

    private final UUID testUUID = UUID.randomUUID();

    @BeforeEach
    void beforeEach() {
        reset(fileNameResolver, s3ClientWrapper);
    }

    @SneakyThrows
    @Test
    void shouldReturnFileStreamWrappedInResponseWithAllHeaders() {

        var responseMetadata = GetObjectResponse.builder().contentLength(25L).build();
        var inputStream = new ByteArrayInputStream("csv,data,here,123,4.3,cat".getBytes());
        var mockS3Response = new ResponseInputStream<>(responseMetadata, inputStream);
        var s3Download = new S3CsvDownload("reports/daily/report_numero_uno_2025-12-13.csv", mockS3Response);

        doNothing().when(reportDao).authorizeReportAccess(testUUID);
        when(fileNameResolver.getS3PrefixFromId(testUUID)).thenReturn("reports/daily/report_numero_uno");
        when(s3ClientWrapper.getResultCsv(any())).thenReturn(Optional.of(s3Download));

        var result = fileDownloadFromS3Service.getFileStreamResponse(testUUID);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        var headers = result.getHeaders();

        assertEquals(MediaType.APPLICATION_OCTET_STREAM, headers.getContentType());
        assertEquals(25L, headers.getContentLength());

        var contentDisposition = headers.getContentDisposition();
        assertTrue(contentDisposition.isAttachment());
        assertEquals("report_numero_uno_2025-12-13.csv", contentDisposition.getFilename());

        var content = new BufferedReader(new InputStreamReader(result.getBody().getInputStream()))
                .lines()
                .collect(Collectors.joining());
        assertEquals("csv,data,here,123,4.3,cat", content);

        verify(fileNameResolver).getS3PrefixFromId(testUUID);
        verify(s3ClientWrapper).getResultCsv("reports/daily/report_numero_uno");
    }

   /* @Test
    void shouldThrowExceptionIfS3ReturnsNoReport() {

        when(reportAccessCheckerService.checkUserCanAccessReport(testUUID)).thenReturn(true);
        when(fileNameResolver.getS3PrefixFromId(testUUID)).thenReturn("reports/daily/report_numero_uno");
        when(s3ClientWrapper.getResultCsv(any())).thenReturn(Optional.empty());

        assertThrows(S3BucketHasNoCopiesOfReportException.class, () -> fileDownloadFromS3Service.getFileStreamResponse(testUUID));

        verify(s3ClientWrapper).getResultCsv("reports/daily/report_numero_uno");

    }

    @Test
    void shouldThrowExceptionIfUserLacksPermissionToAccessReport() {
        when(reportAccessCheckerService.checkUserCanAccessReport(testUUID)).thenThrow(new ReportAccessException(testUUID));

        assertThrows(ReportAccessException.class, () -> fileDownloadFromS3Service.getFileStreamResponse(testUUID));

        verify(reportAccessCheckerService).checkUserCanAccessReport(testUUID);
        verifyNoInteractions(fileNameResolver);
        verifyNoInteractions(s3ClientWrapper);
    }*/

    @Test
    void shouldLetExceptionHandlerHandleExceptionThrownByFileNameResolver() {
        when(fileNameResolver.getS3PrefixFromId(testUUID)).thenThrow(new IllegalArgumentException("Report ID cannot be null or blank"));
        doNothing().when(reportDao).authorizeReportAccess(testUUID);

        var exception = assertThrows(IllegalArgumentException.class, () -> fileDownloadFromS3Service.getFileStreamResponse(testUUID));
        assertEquals("Report ID cannot be null or blank", exception.getMessage());

        verifyNoInteractions(s3ClientWrapper);
    }

    @Test
    void shouldLetExceptionHandlerHandleExceptionThrownByS3Client() {
        doNothing().when(reportDao).authorizeReportAccess(testUUID);
        when(fileNameResolver.getS3PrefixFromId(testUUID)).thenReturn("prefix");
        when(s3ClientWrapper.getResultCsv("prefix")).thenThrow(NoSuchKeyException.builder().build());

        assertThrows(NoSuchKeyException.class, () -> fileDownloadFromS3Service.getFileStreamResponse(testUUID));
    }

}