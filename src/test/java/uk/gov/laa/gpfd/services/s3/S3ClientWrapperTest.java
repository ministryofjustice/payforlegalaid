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
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3ClientWrapperTest {

    @Mock
    private S3Client s3Client;

    @Test
    void shouldGetTemplateFromS3AndReturnInputStream() {

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
    void shouldLetAwsExceptionBeCaughtByExceptionHandler() {

        when(s3Client.getObject(any(GetObjectRequest.class))).thenThrow(NoSuchKeyException.builder().build());
        var s3ClientWrapper = new S3ClientWrapper(s3Client, "bucket");

        assertThrows(NoSuchKeyException.class, () -> s3ClientWrapper.getTemplate("file.xlsx"));

    }

}