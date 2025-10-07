package uk.gov.laa.gpfd.integration;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import uk.gov.laa.gpfd.integration.config.TestS3Config;

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = RANDOM_PORT, classes = {TestS3Config.class})
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {"gpfd.s3.has-s3-access=true", "AWS_REGION=eu-west-1", "S3_FILE_STORE=test"})
@TestInstance(PER_CLASS)
final class ReportGetFileIT extends BaseIT {

    @Autowired
    private S3Client s3Client;

    @Test
    @SneakyThrows
    void shouldSuccessfullyPassStreamReturnedFromAWSToUser() {
        var responseMetadata = GetObjectResponse.builder().contentLength(25L).build();
        var inputStream = new ByteArrayInputStream("csv,data,here,123,4.3,cat".getBytes());
        var mockS3Response = new ResponseInputStream<>(responseMetadata, inputStream);

        when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(mockS3Response);

        performGetRequest("/reports/cc55e276-97b0-4dd8-a919-26d4aa373266/file")
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_OCTET_STREAM))
                .andExpect(content().string("csv,data,here,123,4.3,cat"));
    }
}
