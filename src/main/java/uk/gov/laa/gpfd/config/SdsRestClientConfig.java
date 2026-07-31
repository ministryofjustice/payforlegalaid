package uk.gov.laa.gpfd.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestClient;

/**
 * Configuration class for when we look at S3 buckets.
 */
@Configuration
@ConditionalOnProperty(name = "gpfd.sds-enabled.enabled", havingValue = "true")
@Profile({"!testauth & !testat"})
public class SdsRestClientConfig {

    @Bean
    RestClient restClient(RestClient.Builder builder) {
        return builder.build();
    }

    @Bean
    RestClient sdsRestClient(
            RestClient.Builder builder, @Value("${app.sds-api.url}") String sdsApiUrl) {
        return builder.baseUrl(sdsApiUrl).build();
    }
}
