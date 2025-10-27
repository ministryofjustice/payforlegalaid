package uk.gov.laa.gpfd.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.s3.S3Client;
import uk.gov.laa.gpfd.services.excel.template.S3TemplateClient;
import uk.gov.laa.gpfd.services.excel.template.TemplateClient;
import uk.gov.laa.gpfd.services.excel.template.TemplateFileNameResolver;
import uk.gov.laa.gpfd.services.s3.FileDownloadFromS3Service;
import uk.gov.laa.gpfd.services.s3.FileDownloadService;
import uk.gov.laa.gpfd.services.s3.ReportAccessCheckerService;
import uk.gov.laa.gpfd.services.s3.ReportFileNameResolver;
import uk.gov.laa.gpfd.services.s3.S3ClientWrapper;

/**
 * Configuration class for when we look at S3 buckets.
 */
@Configuration
@ConditionalOnProperty(name = "gpfd.s3.has-s3-access", havingValue = "true")
public class S3Config {

    // When SILAS RBAC is introduced we can replace these with storing permissions in database.
    @Value("${gpfd.s3.permissions.rep000}")
    private String rep000GroupId;

    @Value("${gpfd.s3.permissions.submission-reconciliation}")
    private String submissionReconciliationGroupId;

    /**
     * Creates a {@link TemplateClient} which returns templates from S3.
     *
     * @param s3ClientWrapper          - an S3 Client
     * @param templateFileNameResolver - a class that maps report templates to filenames
     * @return a {@link S3TemplateClient} instance
     */
    @Bean
    public TemplateClient s3TemplateClient(@Qualifier("createS3TemplateClient") S3ClientWrapper s3ClientWrapper, TemplateFileNameResolver templateFileNameResolver) {
        return new S3TemplateClient(s3ClientWrapper, templateFileNameResolver);
    }

    /**
     * Creates a {@link S3ClientWrapper}, a simple object that wraps around the AWS {@link S3Client}
     * This one is for the template store bucket
     *
     * @param awsRegion - region S3 bucket is in
     * @return an object which contains an S3Client with some of our custom config
     */
    @Bean
    public S3ClientWrapper createS3TemplateClient(@Value("${AWS_REGION}") String awsRegion, @Value("${S3_TEMPLATE_STORE}") String fileStore) {
        return new S3ClientWrapper(awsRegion, fileStore);
    }

    /**
     * Creates a {@link S3ClientWrapper}, a simple object that wraps around the AWS {@link S3Client}
     * This one is for the reports bucket
     *
     * @param awsRegion - region S3 bucket is in
     * @return an object which contains an S3Client with some of our custom config
     */
    @Bean
    public S3ClientWrapper createS3ReportClient(@Value("${AWS_REGION}") String awsRegion, @Value("${S3_REPORT_STORE}") String fileStore) {
        return new S3ClientWrapper(awsRegion, fileStore);
    }

    /**
     * Creates a {@link FileDownloadService}, an object that handles S3 file requests and so allows different behaviour on local vs live systems.
     *
     * @param s3ClientWrapper - an S3 Client
     * @return an object that determines how file download should behave for this system
     */
    @Bean
    public FileDownloadService createFileDownloadService(@Qualifier("createS3ReportClient") S3ClientWrapper s3ClientWrapper) {
        return new FileDownloadFromS3Service(s3ClientWrapper, new ReportFileNameResolver(), new ReportAccessCheckerService(rep000GroupId, submissionReconciliationGroupId));
    }

}
