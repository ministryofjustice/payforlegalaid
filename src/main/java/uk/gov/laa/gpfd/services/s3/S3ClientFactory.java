package uk.gov.laa.gpfd.services.s3;

import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.time.Duration;

public class S3ClientFactory {

    public S3Client createS3Client(String awsRegion) {
        // By default, AWS does not time out API calls. Set some to avoid any risk of calls hanging
        var config = ClientOverrideConfiguration.builder()
                .apiCallAttemptTimeout(Duration.ofSeconds(3))
                .apiCallTimeout(Duration.ofSeconds(10))
                .build();

        return S3Client.builder()
                .region(Region.of(awsRegion))
                .overrideConfiguration(config)
                .build();
    }

}
