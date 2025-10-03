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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileDownloadFromS3ServiceTest {

    @Mock
    private S3ClientWrapper s3ClientWrapper;

    @Mock
    private ReportFileNameResolver fileNameResolver;

    @InjectMocks
    private FileDownloadFromS3Service fileDownloadFromS3Service;

    private final UUID testUUID = UUID.randomUUID();
    private final String testFilename = "report_numero_uno.csv";

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

        when(fileNameResolver.getFileNameFromId(testUUID)).thenReturn(testFilename);
        when(s3ClientWrapper.getResultCsv(testFilename)).thenReturn(mockS3Response);

        var result = fileDownloadFromS3Service.getFileStreamResponse(testUUID);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        var headers = result.getHeaders();

        assertEquals(MediaType.APPLICATION_OCTET_STREAM, headers.getContentType());
        assertEquals(25L, headers.getContentLength());

        var contentDisposition = headers.getContentDisposition();
        assertTrue(contentDisposition.isAttachment());
        assertEquals(testFilename, contentDisposition.getFilename());

        var content = new BufferedReader(new InputStreamReader(result.getBody().getInputStream()))
                .lines()
                .collect(Collectors.joining());
        assertEquals("csv,data,here,123,4.3,cat", content);

        verify(fileNameResolver).getFileNameFromId(testUUID);
        verify(s3ClientWrapper).getResultCsv(testFilename);

    }

    @Test
    void shouldLetExceptionHandlerHandleExceptionThrownByFileNameResolver() {
        when(fileNameResolver.getFileNameFromId(testUUID)).thenThrow(new IllegalArgumentException("Report ID cannot be null or blank"));

        assertThrows(IllegalArgumentException.class, () -> fileDownloadFromS3Service.getFileStreamResponse(testUUID),
                "Report ID cannot be null or blank");

        verifyNoInteractions(s3ClientWrapper);
    }

    @Test
    void shouldLetExceptionHandlerHandleExceptionThrownByS3Client() {
        when(fileNameResolver.getFileNameFromId(testUUID)).thenReturn(testFilename);
        when(s3ClientWrapper.getResultCsv(testFilename)).thenThrow(NoSuchKeyException.builder().build());

        assertThrows(NoSuchKeyException.class, () -> fileDownloadFromS3Service.getFileStreamResponse(testUUID));
    }

}