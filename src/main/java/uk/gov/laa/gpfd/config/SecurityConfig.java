package uk.gov.laa.gpfd.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import uk.gov.laa.gpfd.config.builders.AuthorizeHttpRequestsBuilder;
import uk.gov.laa.gpfd.config.builders.HttpSecuritySessionManagementConfigurerBuilder;
import uk.gov.laa.gpfd.config.builders.SessionManagementConfigurerBuilder;

import static com.azure.spring.cloud.autoconfigure.implementation.aad.security.AadWebApplicationHttpSecurityConfigurer.aadWebApplication;

/**
 * Configuration class to set up Spring Security for the application.
 * <p>
 * This class configures the core security settings for HTTP requests, including
 * authorization, session management, and HTTP basic authentication. The configuration
 * is modularized into different components, such as {@link AuthorizeHttpRequestsBuilder}
 * and {@link SessionManagementConfigurerBuilder}, which are injected into this class
 * to manage specific security aspects.
 * </p>
 */
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.cloud.azure.active-directory.enabled", havingValue = "true")
public class SecurityConfig {

    private final AuthorizationManager<RequestAuthorizationContext> authManager;
    private final HttpSecuritySessionManagementConfigurerBuilder concurrencyControlConfigurerCustomizer;

    /**
     * Configures the {@link SecurityFilterChain} for the HTTP security settings.
     * <p>
     * This method customizes the security filter chain by applying the authorization
     * rules, enabling HTTP basic authentication, and applying the session management
     * configuration to control session concurrency and expiration.
     * We create the customisers in the function as Bean customisers are automatically implemented by Spring Security 7,
     * and running each customiser twice can cause issues.
     * </p>
     *
     * @param httpSecurity the {@link HttpSecurity} object used to configure HTTP security.
     * @return a configured {@link SecurityFilterChain} object.
     * @throws Exception if any error occurs during the configuration of HTTP security.
     */
    @Bean
    SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        var authorizeHttpRequestsBuilder = new AuthorizeHttpRequestsBuilder(authManager);
        var sessionManagementConfigurerBuilder = new SessionManagementConfigurerBuilder(concurrencyControlConfigurerCustomizer);
        return httpSecurity
                .with(aadWebApplication())
                .authorizeHttpRequests(authorizeHttpRequestsBuilder)    // Apply authorization rules
                .sessionManagement(sessionManagementConfigurerBuilder)  // Apply session management configuration
                .build();
    }

}