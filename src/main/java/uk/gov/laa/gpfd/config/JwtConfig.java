package uk.gov.laa.gpfd.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;

@Configuration
@ConditionalOnProperty(name = "spring.cloud.azure.active-directory.enabled", havingValue = "true")
public class JwtConfig {

    private final AppConfig appConfig;

    public JwtConfig(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    /**
     * The custom {@link JwtDecoder} This {@link JwtDecoder} bean is configured to use the JWKS endpoint provided by
     *  * Microsoft Identity Platform, typically located at:
     *  * <pre>
     *  * https://login.microsoftonline.com/{tenantId}/discovery/v2.0/keys
     *  * </pre>
     * @return a {@link JwtDecoder} that validates JWTs signed by Entra Id
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        return JwtDecoders.fromIssuerLocation(appConfig.getJwksUri());
    }

}