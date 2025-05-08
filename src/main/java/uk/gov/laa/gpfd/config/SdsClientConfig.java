package uk.gov.laa.gpfd.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Configuration class for secure document storage integration config, beans and settings.
 * <p>
 * This class defines bean for RestClient builder. This is required for connectivity to
 * secure document storage service, and connectivity to AWS S3 bucket where report templates are stored.
 * </p>
 */
@Configuration
public class SdsClientConfig {

    /**
     * Configures a {@RestClient.Builder}
     * <p>
     * This bean is intended to allow connectivity to multiple RestClients, including
     * SDS service and AWS S3 bucket.
     * </p>
     *
     * @return a configured {@link RestClient.Builder} for connectivity.
     */
    @Bean
    public RestClient.Builder restClient() {
        return RestClient.builder();
    }

    @Getter
    @Value("${sds-client.sds-url}")
    private String sdsUrl;

}
