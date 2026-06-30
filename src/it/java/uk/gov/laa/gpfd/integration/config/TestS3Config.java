package uk.gov.laa.gpfd.integration.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.services.s3.S3Client;
import uk.gov.laa.gpfd.dao.ReportDao;
import uk.gov.laa.gpfd.services.excel.template.S3TemplateClient;
import uk.gov.laa.gpfd.services.excel.template.TemplateClient;
import uk.gov.laa.gpfd.services.excel.template.TemplateFileNameResolver;
import uk.gov.laa.gpfd.services.s3.FileDownloadFromS3Service;
import uk.gov.laa.gpfd.services.s3.FileDownloadService;
import uk.gov.laa.gpfd.services.s3.ReportFileNameResolver;
import uk.gov.laa.gpfd.services.s3.S3ClientWrapper;

import static org.mockito.Mockito.mock;

@TestConfiguration
@Profile("!testat")
public class TestS3Config {

    @Bean
    public S3ClientWrapper createS3TemplateClient() {
        return new S3ClientWrapper(mockS3Client(), "test-bucket");
    }

    @Bean
    public S3ClientWrapper createS3ReportClient() {
        return new S3ClientWrapper(mockS3Client(), "test-bucket-reports");
    }

    @Bean
    public S3Client mockS3Client() {
        return mock(S3Client.class);
    }

    @Bean
    public FileDownloadService createFileDownloadService(@Qualifier("createS3ReportClient") S3ClientWrapper s3ClientWrapper, ReportDao reportDao) {
        return new FileDownloadFromS3Service(s3ClientWrapper, new ReportFileNameResolver(), reportDao);
    }

    @Bean
    public TemplateClient s3TemplateClient(@Qualifier("createS3TemplateClient") S3ClientWrapper s3ClientWrapper, TemplateFileNameResolver templateFileNameResolver) {
        return new S3TemplateClient(s3ClientWrapper, templateFileNameResolver);
    }
}
