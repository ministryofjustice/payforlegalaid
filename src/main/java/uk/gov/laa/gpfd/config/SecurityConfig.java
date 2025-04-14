package uk.gov.laa.gpfd.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
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
@ConditionalOnProperty(name = "spring.cloud.azure.active-directory.enabled", havingValue = "true")
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

    private final AuthorizationManager<RequestAuthorizationContext> authManager;

    /**
     * The custom {@link JwtDecoder} used to decode and validate JSON Web Tokens (JWTs) in a Spring Security
     *  context.
     */
    private final JwtDecoder jwtDecoder;

    /**
     * {@link AppConfig} is a configuration class that encapsulates application-level
     *  * settings and properties. Configuration loaded from application yml file.
     */
    private final AppConfig appConfig;

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
    SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                //Validate JWT first - if it's not valid, it will fall back to normal session cookie or log in flows
                .addFilterBefore(new JwtAuthenticationFilter(jwtDecoder, appConfig), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(authorizeHttpRequestsBuilder)    // Apply authorization rules
                .sessionManagement(sessionManagementConfigurerBuilder)  // Apply session management configuration
                .build();
    }

    @Bean
    public DelegatingSecurityContextAsyncTaskExecutor taskExecutor(ThreadPoolTaskExecutor delegate) {
        return new DelegatingSecurityContextAsyncTaskExecutor(delegate);
    }

}