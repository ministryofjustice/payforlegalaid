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

    @Bean
    public JwtDecoder jwtDecoder() {
        // This will validate the issuer and signature based on the Entra setup, to ensure token validity
        return JwtDecoders.fromIssuerLocation(appConfig.getJwksUri());
    }

}
