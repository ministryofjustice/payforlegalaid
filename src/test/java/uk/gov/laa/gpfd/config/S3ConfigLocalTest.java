package uk.gov.laa.gpfd.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import uk.gov.laa.gpfd.services.excel.template.LocalTemplateClient;
import uk.gov.laa.gpfd.services.excel.template.TemplateClient;
import uk.gov.laa.gpfd.services.s3.FileDownloadLocalService;
import uk.gov.laa.gpfd.services.s3.FileDownloadService;
import uk.gov.laa.gpfd.services.s3.S3ClientWrapper;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "gpfd.s3.has-s3-access=false"
})
class S3ConfigLocalTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private TemplateClient templateClient;

    @Autowired
    private FileDownloadService fileDownloadService;

    @Test
    void shouldHaveALocalTemplateClient() {
        assertInstanceOf(LocalTemplateClient.class, templateClient);
    }

    @Test
    void shouldNotHaveAS3Client() {
        assertThrows(NoSuchBeanDefinitionException.class, () -> context.getBean(S3ClientWrapper.class));
    }

    @Test
    void shouldHaveAFileDownloadLocalService() {
        assertInstanceOf(FileDownloadLocalService.class, fileDownloadService);
    }

}