package uk.gov.laa.gpfd.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import uk.gov.laa.gpfd.config.builders.AuthorizeHttpRequestsBuilder;
import uk.gov.laa.gpfd.config.builders.SessionManagementConfigurerBuilder;

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
public class SecurityConfig {

    /**
     * The custom {@link AuthorizeHttpRequestsBuilder} responsible for configuring
     * the authorization rules for HTTP requests, such as which endpoints are publicly
     * accessible and which require authentication.
     * This dependency is injected via constructor injection due to the
     * {@link RequiredArgsConstructor} annotation.
     */
    private final AuthorizeHttpRequestsBuilder authorizeHttpRequestsBuilder;

    /**
     * The custom {@link SessionManagementConfigurerBuilder} responsible for configuring
     * session management, including session concurrency control and session expiration.
     * This dependency is injected via constructor injection due to the
     * {@link RequiredArgsConstructor} annotation.
     */
    private final SessionManagementConfigurerBuilder sessionManagementConfigurerBuilder;

    /**
     * Configures the {@link SecurityFilterChain} for the HTTP security settings.
     * <p>
     * This method customizes the security filter chain by applying the authorization
     * rules, enabling HTTP basic authentication, and applying the session management
     * configuration to control session concurrency and expiration.
     * </p>
     *
     * @param httpSecurity the {@link HttpSecurity} object used to configure HTTP security.
     * @return a configured {@link SecurityFilterChain} object.
     * @throws Exception if any error occurs during the configuration of HTTP security.
     */
    @Bean
    @ConditionalOnProperty(name = "spring.cloud.azure.active-directory.enabled", havingValue = "true")
    SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .authorizeHttpRequests(authorizeHttpRequestsBuilder)    // Apply authorization rules
                .sessionManagement(sessionManagementConfigurerBuilder)  // Apply session management configuration
                .build();
    }

}