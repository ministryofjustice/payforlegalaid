package uk.gov.laa.gpfd.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.laa.gpfd.services.excel.template.LocalTemplateClient;
import uk.gov.laa.gpfd.services.excel.template.TemplateClient;
import uk.gov.laa.gpfd.services.excel.template.TemplateFileNameResolver;
import uk.gov.laa.gpfd.services.s3.FileDownloadLocalService;
import uk.gov.laa.gpfd.services.s3.FileDownloadService;

/**
 * Configuration class for local version of S3 Config.
 * In practice this doesn't do much other than turn off the feature on local as we have no S3 mocking currently.
 */
@Configuration
@ConditionalOnProperty(name = "gpfd.s3.use-template-store", havingValue = "false", matchIfMissing = true)
public class S3ConfigLocal {

    /**
     * Creates a {@link TemplateClient} which returns a local template.
     * For environments with s3.use-template-store enabled, see {@link S3Config} for bean-creation.
     *
     * @return a {@link LocalTemplateClient} instance
     */
    @Bean
    public TemplateClient localTemplateClient(TemplateFileNameResolver templateFileNameResolver) {
        return new LocalTemplateClient(templateFileNameResolver);
    }

    /**
     * Creates a {@link FileDownloadService}, an object that handles S3 file requests and so allows different behaviour on local vs live systems.
     *
     * @return an object that determines how file download should behave for local
     */
    @Bean
    public FileDownloadService getFileDownloadService() {
        return new FileDownloadLocalService();
    }

}
