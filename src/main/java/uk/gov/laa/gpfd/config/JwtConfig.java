package uk.gov.laa.gpfd.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import uk.gov.laa.gpfd.config.builders.SessionManagementConfigurerBuilder;

@Configuration
@ConditionalOnProperty(name = "spring.cloud.azure.active-directory.enabled", havingValue = "true")
public class JwtConfig {

    private final AppConfig appConfig;

    public JwtConfig(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    /**
     * The custom {@link JwtDecoder} responsible for validating
     * tokens provided by Entra ID. This will validate the issuer and signature based
     * on the Entra ID setup, to ensure token validity
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        return JwtDecoders.fromIssuerLocation(appConfig.getJwksUri());
    }

}
