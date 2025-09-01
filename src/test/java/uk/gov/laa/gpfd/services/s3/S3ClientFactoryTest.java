package uk.gov.laa.gpfd.services.s3;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.time.Duration;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class S3ClientFactoryTest {

    private final S3ClientFactory s3ClientFactory = new S3ClientFactory();
    private S3Client createdClient;

    @AfterEach
    void cleanupCreatedClient() {
        createdClient.close();
    }

    @Test
    void shouldCreateS3ClientWithPassedInRegion() {

        createdClient = s3ClientFactory.createS3Client("eu-west-1");

        assertEquals(Region.EU_WEST_1, createdClient.serviceClientConfiguration().region());

    }

    @Test
    void shouldCreateS3ClientWithApiTimeoutsSet() {

        createdClient = s3ClientFactory.createS3Client("eu-west-1");

        assertEquals(Optional.of(Duration.ofSeconds(10)), createdClient.serviceClientConfiguration().overrideConfiguration().apiCallTimeout());
        assertEquals(Optional.of(Duration.ofSeconds(3)), createdClient.serviceClientConfiguration().overrideConfiguration().apiCallAttemptTimeout());

    }
}
