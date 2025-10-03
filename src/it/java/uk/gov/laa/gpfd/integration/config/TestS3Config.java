package uk.gov.laa.gpfd.integration.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import software.amazon.awssdk.services.s3.S3Client;
import uk.gov.laa.gpfd.services.s3.S3ClientWrapper;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestS3Config {

    @Bean
    @Primary
    public S3ClientWrapper mockS3ClientWrapper() {
        return new S3ClientWrapper(mockS3Client(), "test-bucket");
    }

    @Bean
    public S3Client mockS3Client() {
        return mock(S3Client.class);
    }
}
