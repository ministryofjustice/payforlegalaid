package uk.gov.laa.gpfd.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.s3.S3Client;
import uk.gov.laa.gpfd.services.excel.template.TemplateFileNameResolver;
import uk.gov.laa.gpfd.services.s3.S3ClientWrapper;
import uk.gov.laa.gpfd.services.excel.template.S3TemplateClient;
import uk.gov.laa.gpfd.services.excel.template.TemplateClient;

/**
 * Configuration class for when we look at S3 buckets.
 */
@Configuration
@ConditionalOnProperty(name = "gpfd.s3.use-template-store", havingValue = "true")
public class S3Config {

    /**
     * Creates a {@link TemplateClient} which returns templates from S3.
     *
     * @return a {@link S3TemplateClient} instance
     */
    @Bean
    public TemplateClient s3TemplateClient(S3ClientWrapper s3ClientWrapper, TemplateFileNameResolver templateFileNameResolver) {
        return new S3TemplateClient(s3ClientWrapper, templateFileNameResolver);
    }

    /**
     * Creates a {@link S3ClientWrapper}, a simple object that wraps around the AWS {@link S3Client}
     *
     * @param awsRegion - region S3 bucket is in
     * @return an object which contains an S3Client with some of our custom config
     */
    @Bean
    public S3ClientWrapper createS3Client(@Value("${AWS_REGION}") String awsRegion, @Value("${S3_FILE_STORE}") String fileStore) {
        return new S3ClientWrapper(awsRegion, fileStore);
    }

}
