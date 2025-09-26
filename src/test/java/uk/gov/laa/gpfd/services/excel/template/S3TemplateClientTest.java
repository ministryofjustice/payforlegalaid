package uk.gov.laa.gpfd.services.excel.template;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import uk.gov.laa.gpfd.exception.TemplateResourceException.TemplateResourceNotFoundException;
import uk.gov.laa.gpfd.services.s3.S3ClientWrapper;

import java.io.ByteArrayInputStream;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3TemplateClientTest {

    @Mock
    private TemplateFileNameResolver templateFileNameResolver;

    @Mock
    private S3ClientWrapper s3ClientWrapper;

    @InjectMocks
    private S3TemplateClient s3TemplateClient;

    private final UUID testUUID = UUID.randomUUID();

    @BeforeEach
    void resetMocks() {
        reset(templateFileNameResolver);
        reset(s3ClientWrapper);
        when(templateFileNameResolver.getFileNameFromId(testUUID)).thenReturn("testTemplate.xlsx");
    }

    @Test
    void shouldReturnInputStreamForValidId() {
        var responseMetadata = GetObjectResponse.builder().build();
        var inputStream = new ByteArrayInputStream("mock template data".getBytes());
        var mockResponse = new ResponseInputStream<>(responseMetadata, inputStream);

        when(s3ClientWrapper.getTemplate("testTemplate.xlsx")).thenReturn(mockResponse);
        assertNotNull(s3TemplateClient.findTemplateById(testUUID));
    }

    @Test
    void shouldReturnNullForNullFilename() {
        when(templateFileNameResolver.getFileNameFromId(testUUID)).thenReturn(null);
        assertNull(s3TemplateClient.findTemplateById(testUUID));
    }

    @Test
    void shouldThrowsExceptionIfNullStreamReturned() {
        when(s3ClientWrapper.getTemplate("testTemplate.xlsx")).thenReturn(null);
        assertThrows(TemplateResourceNotFoundException.class, () -> s3TemplateClient.findTemplateById(testUUID));
    }

    @Test
    void shouldLetAwsExceptionBeCaughtByExceptionHandler() {

        when(s3ClientWrapper.getTemplate(any())).thenThrow(NoSuchKeyException.builder().build());

        assertThrows(NoSuchKeyException.class, () -> s3TemplateClient.findTemplateById(testUUID));

    }

}