//package uk.gov.laa.gpfd.services.s3;
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import software.amazon.awssdk.core.ResponseInputStream;
//import software.amazon.awssdk.services.s3.S3Client;
//import software.amazon.awssdk.services.s3.model.GetObjectRequest;
//import software.amazon.awssdk.services.s3.model.GetObjectResponse;
//import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
//import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
//import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
//import software.amazon.awssdk.services.s3.model.S3Object;
//
//import java.io.ByteArrayInputStream;
//import java.time.Instant;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class S3ClientWrapperTest {
//
//    @Mock
//    private S3Client s3Client;
//
//    @Test
//    void getTemplate_shouldGetFileFromS3AndReturnInputStream() {
//
//        var responseMetadata = GetObjectResponse.builder().build();
//        var inputStream = new ByteArrayInputStream("mock template data".getBytes());
//        var mockResponse = new ResponseInputStream<>(responseMetadata, inputStream);
//
//        when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(mockResponse);
//        var s3ClientWrapper = new S3ClientWrapper(s3Client, "bucket");
//
//        var result = s3ClientWrapper.getTemplate("file.xlsx");
//
//        assertEquals(mockResponse, result);
//
//        // Check wrapper builds up the correct request to S3
//        var captor = ArgumentCaptor.forClass(GetObjectRequest.class);
//        verify(s3Client).getObject(captor.capture());
//        var requestToS3 = captor.getValue();
//        assertEquals("bucket", requestToS3.bucket());
//        assertEquals("templates/file.xlsx", requestToS3.key());
//
//    }
//
//    @Test
//    void getTemplate_shouldLetAwsExceptionBeCaughtByExceptionHandler() {
//
//        when(s3Client.getObject(any(GetObjectRequest.class))).thenThrow(NoSuchKeyException.builder().build());
//        var s3ClientWrapper = new S3ClientWrapper(s3Client, "bucket");
//
//        assertThrows(NoSuchKeyException.class, () -> s3ClientWrapper.getTemplate("file.xlsx"));
//
//    }
//
//    @Test
//    void getResultCsv_shouldGetFileFromS3AndReturnInputStream() {
//
//        var responseMetadata = GetObjectResponse.builder().build();
//        var inputStream = new ByteArrayInputStream("mock,csv,data".getBytes());
//        var mockResponse = new ResponseInputStream<>(responseMetadata, inputStream);
//
//        when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(mockResponse);
//        var s3ClientWrapper = new S3ClientWrapper(s3Client, "bucket");
//
//        var list = List.of(
//                S3Object.builder().key("reports/daily/report_2025-12-14.csv").lastModified(Instant.parse("2025-12-14T05:00:00Z")).build(),
//                S3Object.builder().key("reports/daily/report_2025-12-15-2.csv").lastModified(Instant.parse("2025-12-15T05:00:01Z")).build(),
//                S3Object.builder().key("reports/daily/report_2025-12-13.csv").lastModified(Instant.parse("2025-12-13T05:00:00Z")).build(),
//                S3Object.builder().key("reports/daily/report_2025-12-15.csv").lastModified(Instant.parse("2025-12-15T05:00:00Z")).build()
//        );
//        var arrayList = new ArrayList<>(list);
//
//        var mockListResponse = ListObjectsV2Response.builder().contents(arrayList).build();
//        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(mockListResponse);
//
//        var result = s3ClientWrapper.getResultCsv("report.csv", "daily", "report");
//
//        assertEquals(mockResponse, result);
//
//        // Check wrapper builds up the correct request to S3
//        var captor = ArgumentCaptor.forClass(GetObjectRequest.class);
//        verify(s3Client).getObject(captor.capture());
//        var requestToS3 = captor.getValue();
//        assertEquals("bucket", requestToS3.bucket());
//        assertEquals("reports/daily/report_2025-12-15-2.csv", requestToS3.key());
//
//    }
//
////    @Test
////    void getResultCsv_shouldLetAwsExceptionBeCaughtByExceptionHandler() {
////
////        when(s3Client.getObject(any(GetObjectRequest.class))).thenThrow(NoSuchKeyException.builder().build());
////        var s3ClientWrapper = new S3ClientWrapper(s3Client, "bucket");
////
////        assertThrows(NoSuchKeyException.class, () -> s3ClientWrapper.getResultCsv("file.csv", "daily"));
////
////    }
//
//}