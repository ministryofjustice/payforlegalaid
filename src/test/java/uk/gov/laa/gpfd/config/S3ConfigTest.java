package uk.gov.laa.gpfd.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import uk.gov.laa.gpfd.services.excel.template.S3TemplateClient;
import uk.gov.laa.gpfd.services.excel.template.TemplateClient;
import uk.gov.laa.gpfd.services.s3.FileDownloadFromS3Service;
import uk.gov.laa.gpfd.services.s3.FileDownloadService;
import uk.gov.laa.gpfd.services.s3.S3ClientWrapper;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "gpfd.s3.has-s3-access=true",
        "AWS_REGION=eu-west-1",
        "S3_TEMPLATE_STORE=test",
        "S3_REPORT_STORE=test2",
        "gpfd.s3.permissions.rep000=fjfh34-fdsff33-fdfj444",
        "gpfd.s3.permissions.submission-reconciliation=jfdsf234-32434fd-34234"
})
class S3ConfigTest {

    @Autowired
    private TemplateClient templateClient;

    @Autowired
    @Qualifier("createS3TemplateClient")
    private S3ClientWrapper s3ClientWrapperTemplates;

    @Autowired
    @Qualifier("createS3ReportClient")
    private S3ClientWrapper s3ClientWrapperReports;

    @Autowired
    private FileDownloadService fileDownloadService;

    @Test
    void shouldHaveAS3TemplateClient() {
        assertInstanceOf(S3TemplateClient.class, templateClient);
    }

    @Test
    void shouldHaveAS3ClientForTemplateBucket() {
        assertNotNull(s3ClientWrapperTemplates);
    }

    @Test
    void shouldHaveAS3ClientForReportBucket() {
        assertNotNull(s3ClientWrapperReports);
    }

    @Test
    void shouldHaveAFileDownloadFromS3Service() {
        assertInstanceOf(FileDownloadFromS3Service.class, fileDownloadService);
    }

}