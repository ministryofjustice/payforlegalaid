package uk.gov.laa.gpfd.services.s3;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3ClientWrapperTest {

    @Mock
    private S3Client s3Client;

    @Test
    void getTemplate_shouldGetFileFromS3AndReturnInputStream() {

        var responseMetadata = GetObjectResponse.builder().build();
        var inputStream = new ByteArrayInputStream("mock template data".getBytes());
        var mockResponse = new ResponseInputStream<>(responseMetadata, inputStream);

        when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(mockResponse);
        var s3ClientWrapper = new S3ClientWrapper(s3Client, "bucket");

        var result = s3ClientWrapper.getTemplate("file.xlsx");

        assertEquals(mockResponse, result);

        // Check wrapper builds up the correct request to S3
        var captor = ArgumentCaptor.forClass(GetObjectRequest.class);
        verify(s3Client).getObject(captor.capture());
        var requestToS3 = captor.getValue();
        assertEquals("bucket", requestToS3.bucket());
        assertEquals("templates/file.xlsx", requestToS3.key());

    }

    @Test
    void getTemplate_shouldLetAwsExceptionBeCaughtByExceptionHandler() {

        when(s3Client.getObject(any(GetObjectRequest.class))).thenThrow(NoSuchKeyException.builder().build());
        var s3ClientWrapper = new S3ClientWrapper(s3Client, "bucket");

        assertThrows(NoSuchKeyException.class, () -> s3ClientWrapper.getTemplate("file.xlsx"));

    }

    @Test
    void getResultCsv_shouldPickLatestMatchingFileFromS3AndReturnInputStream() {

        var matchingFileList = new ArrayList<>(List.of(
                S3Object.builder().key("reports/daily/report000_2025-12-14.csv").lastModified(Instant.parse("2025-12-14T05:00:00Z")).build(),
                S3Object.builder().key("reports/daily/report000_2025-12-15-2.csv").lastModified(Instant.parse("2025-12-15T05:00:01Z")).build(),
                S3Object.builder().key("reports/daily/report000_2025-12-13.csv").lastModified(Instant.parse("2025-12-13T05:00:00Z")).build(),
                S3Object.builder().key("reports/daily/report000_2025-12-15.csv").lastModified(Instant.parse("2025-12-15T05:00:00Z")).build()
        ));
        var mockListResponse = ListObjectsV2Response.builder().contents(matchingFileList).build();
        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(mockListResponse);

        var responseMetadata = GetObjectResponse.builder().build();
        var inputStream = new ByteArrayInputStream("mock,csv,data".getBytes());
        var mockResponse = new ResponseInputStream<>(responseMetadata, inputStream);
        when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(mockResponse);

        var s3ClientWrapper = new S3ClientWrapper(s3Client, "bucket");
        var resultOptional = s3ClientWrapper.getResultCsv("reports/daily/report000");
        assertTrue(resultOptional.isPresent());

        var result = resultOptional.get();
        assertEquals(mockResponse, result.stream());
        assertEquals("report000_2025-12-15-2.csv", result.getFileName());

        // Check wrapper builds up the correct List request to S3
        var listObjectCaptor = ArgumentCaptor.forClass(ListObjectsV2Request.class);
        verify(s3Client).listObjectsV2(listObjectCaptor.capture());
        var listObjectRequest = listObjectCaptor.getValue();
        assertEquals("bucket", listObjectRequest.bucket());
        assertEquals("reports/daily/report000", listObjectRequest.prefix());

        // Check wrapper builds up the correct Download request to S3
        var getObjectCaptor = ArgumentCaptor.forClass(GetObjectRequest.class);
        verify(s3Client).getObject(getObjectCaptor.capture());
        var getObjectRequest = getObjectCaptor.getValue();
        assertEquals("bucket", getObjectRequest.bucket());
        assertEquals("reports/daily/report000_2025-12-15-2.csv", getObjectRequest.key());

    }

    @Test
    void getResultCsv_shouldFilterOutFilesThatAreNotCsv() {

        var matchingFileList = new ArrayList<>(List.of(
                S3Object.builder().key("reports/daily/report_2025-12-14.csv").lastModified(Instant.parse("2025-12-14T05:00:00Z")).build(),
                S3Object.builder().key("reports/daily/report_2025-12-15.xlsx").lastModified(Instant.parse("2025-12-15T05:00:00Z")).build()
        ));
        var mockListResponse = ListObjectsV2Response.builder().contents(matchingFileList).build();
        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(mockListResponse);

        var responseMetadata = GetObjectResponse.builder().build();
        var inputStream = new ByteArrayInputStream("mock,csv,data".getBytes());
        var mockResponse = new ResponseInputStream<>(responseMetadata, inputStream);
        when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(mockResponse);

        var s3ClientWrapper = new S3ClientWrapper(s3Client, "bucket");
        var resultOptional = s3ClientWrapper.getResultCsv("report");
        assertTrue(resultOptional.isPresent());

        var result = resultOptional.get();

        assertEquals(mockResponse, result.stream());
        assertEquals("report_2025-12-14.csv", result.getFileName());

        // Check wrapper builds up the correct request to S3
        var captor = ArgumentCaptor.forClass(GetObjectRequest.class);
        verify(s3Client).getObject(captor.capture());
        var requestToS3 = captor.getValue();
        assertEquals("bucket", requestToS3.bucket());
        assertEquals("reports/daily/report_2025-12-14.csv", requestToS3.key());

    }

    @Test
    void getResultCsv_shouldReturnEmptyOptionIfS3ReturnsEmptyList() {

        var matchingFileList = new ArrayList<S3Object>();
        var mockListResponse = ListObjectsV2Response.builder().contents(matchingFileList).build();
        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(mockListResponse);

        var s3ClientWrapper = new S3ClientWrapper(s3Client, "bucket");
        var resultOptional = s3ClientWrapper.getResultCsv("reports/daily/report000");
        assertTrue(resultOptional.isEmpty());
    }

    @Test
    void getResultCsv_shouldReturnEmptyOptionIfNoCsvFilesAvailable() {

        var matchingFileList = new ArrayList<>(List.of(
                S3Object.builder().key("reports/daily/report_2025-12-14.ppt").lastModified(Instant.parse("2025-12-14T05:00:00Z")).build(),
                S3Object.builder().key("reports/daily/report_2025-12-15.xlsx").lastModified(Instant.parse("2025-12-15T05:00:00Z")).build()
        ));
        var mockListResponse = ListObjectsV2Response.builder().contents(matchingFileList).build();
        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(mockListResponse);

        var s3ClientWrapper = new S3ClientWrapper(s3Client, "bucket");
        var resultOptional = s3ClientWrapper.getResultCsv("reports/daily/report000");
        assertTrue(resultOptional.isEmpty());
    }

    @Test
    void getResultCsv_shouldLetAwsExceptionFromListObjectsBeCaughtByExceptionHandler() {

        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenThrow(NoSuchKeyException.builder().build());
        var s3ClientWrapper = new S3ClientWrapper(s3Client, "bucket");

        assertThrows(NoSuchKeyException.class, () -> s3ClientWrapper.getResultCsv("reports/daily/file"));

    }

    @Test
    void getResultCsv_shouldLetAwsExceptionFromGetObjectBeCaughtByExceptionHandler() {

        var matchingFileList = new ArrayList<>(List.of(
                S3Object.builder().key("reports/daily/report_2025-12-15.csv").lastModified(Instant.parse("2025-12-15T05:00:00Z")).build()
        ));
        var mockListResponse = ListObjectsV2Response.builder().contents(matchingFileList).build();
        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(mockListResponse);

        when(s3Client.getObject(any(GetObjectRequest.class))).thenThrow(NoSuchKeyException.builder().build());
        var s3ClientWrapper = new S3ClientWrapper(s3Client, "bucket");

        assertThrows(NoSuchKeyException.class, () -> s3ClientWrapper.getResultCsv("reports/daily/file"));

    }

    @Test
    void S3CsvDownload_getFileName_shouldStripFoldersFromS3Key() {
        var stream = mock(ResponseInputStream.class);
        var s3CsvDownload = new S3ClientWrapper.S3CsvDownload("reports/daily/report_000_2025-12-12.csv", stream);
        assertEquals("report_000_2025-12-12.csv", s3CsvDownload.getFileName());
    }
}