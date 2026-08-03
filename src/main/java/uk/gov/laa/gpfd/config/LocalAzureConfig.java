package uk.gov.laa.gpfd.config;

import com.azure.core.credential.TokenCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Local profile configuration that provides a real Azure TokenCredential using
 * client credentials flow for SDS.
 * <p>
 * This avoids the DefaultAzureCredential chain (IntelliJ, CLI, Managed Identity, etc.)
 * which is unavailable inside Docker, while still obtaining real tokens for calling
 * the SDS test environment.
 */
@Slf4j
@Configuration
@Profile("local")
@ConditionalOnProperty(name = "gpfd.sds-enabled.enabled", havingValue = "true")
public class LocalAzureConfig {

    @Value("${SDS_TENANT_ID:${AZURE_TENANT_ID:${AUTH_TENANT_ID:${TENANT_ID}}}}")
    private String tenantId;

    @Value("${SDS_API_CLIENT_ID:${AZURE_CLIENT_ID:${CLIENT_ID}}}")
    private String clientId;

    @Value("${SDS_API_CLIENT_SECRET:${AZURE_CLIENT_SECRET:${CLIENT_SECRET}}}")
    private String clientSecret;

    /**
     * Provides a {@link TokenCredential} backed by the client credentials grant
     * using the SDS client credentials configured for the local profile.
     * <p>
     * This replaces the DefaultAzureCredential that is auto-configured by
     * spring-cloud-azure-starter-active-directory, which fails inside Docker
     * because none of the chained credential types (IntelliJ, VS Code, CLI, etc.)
     * are available.
     *
     * @return a real Azure {@link TokenCredential} using client secret credentials
     */
    @Bean
    public TokenCredential tokenCredential() {
        log.info("Local profile: building ClientSecretCredential for tenant={}, clientId={}", tenantId, clientId);
        return new ClientSecretCredentialBuilder()
                .tenantId(tenantId)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .build();
    }
}