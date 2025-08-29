package uk.gov.laa.gpfd.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import uk.gov.laa.gpfd.services.excel.template.S3TemplateClient;
import uk.gov.laa.gpfd.services.excel.template.TemplateClient;

@Configuration
@ConditionalOnProperty(name = "gpfd.s3.use-template-store", havingValue = "true")
public class S3Config {

    /**
     * Creates a {@link TemplateClient} which returns templates from S3.
     *
     * @return a {@link S3TemplateClient} instance
     */
    @Bean
    public TemplateClient s3TemplateClient(S3Client s3Client) {
        return new S3TemplateClient(s3Client);
    }

    /**
     * Creates a {@link S3Client}
     *
     * @param awsRegion - region S3 bucket is in
     * @return a S3Client
     */
    @Bean
    public S3Client createS3Client(@Value("${AWS_REGION}") String awsRegion){
        return S3Client.builder()
                .region(Region.of(awsRegion))
                .build();
    }

}
