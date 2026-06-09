package uk.gov.laa.gpfd.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import uk.gov.laa.gpfd.config.builders.AuthorizeHttpRequestsBuilder;
import uk.gov.laa.gpfd.config.builders.HttpSecuritySessionManagementConfigurerBuilder;
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
@SuppressWarnings("java:S4502") // CSRF disabled only for CSP report POST endpoint
@Configuration
public class SecurityConfig {

    private final AuthorizationManager<RequestAuthorizationContext> authManager;
    private final HttpSecuritySessionManagementConfigurerBuilder concurrencyControlConfigurerCustomizer;
    private final CookieCsrfTokenRepository csrfTokenRepository;

    @Value("${gpfd.security.cors.allowed-origin:https://127.0.0.1:8080}")
    private String allowedCorsOrigin;

    @Value("${swagger-ui.enabled:true}")
    private boolean swaggerEnabled;

    public SecurityConfig(AuthorizationManager<RequestAuthorizationContext> authManager, HttpSecuritySessionManagementConfigurerBuilder concurrencyControlConfigurerCustomizer, CookieCsrfTokenRepository csrfTokenRepository) {
        this.authManager = authManager;
        this.concurrencyControlConfigurerCustomizer = concurrencyControlConfigurerCustomizer;
        this.csrfTokenRepository = csrfTokenRepository;
    }

    /**
     * Configures a dedicated security filter chain for static assets.
     *
     * <p>
     * Static resources such as CSS, JavaScript, images, MoJ and GOV.UK frontend assets
     * are served through a separate higher-priority filter chain to avoid inheriting
     * the cache-control headers used for authenticated application responses.
     * </p>
     *
     * <p>
     * This separation allows browsers to cache static assets efficiently while
     * preserving strict no-store policies for sensitive authenticated content.
     * </p>
     *
     * @param http the {@link HttpSecurity} object used to configure security for static resources
     * @return a configured {@link SecurityFilterChain} for static resource requests
     * @throws Exception if an error occurs while building the security filter chain
     */
    @Bean
    @Order(1)
    SecurityFilterChain staticChain(HttpSecurity http) throws Exception {
        return SecurityConfigSupport.createStaticChain(http);
    }

    /**
     * Configures the {@link SecurityFilterChain} for the HTTP security settings.
     * <p>
     * This method customizes the security filter chain by applying the authorization
     * rules, enabling HTTP basic authentication, and applying the session management
     * configuration to control session concurrency and expiration.
     * We create the customizers in the function as Bean customizers are automatically implemented by Spring Security 7,
     * and running each customizer twice can cause issues.
     * Static resources are configured using a separate filter chain to ensure
     * asset caching remains independent of authenticated response cache policies.
     * </p>
     *
     * @param httpSecurity the {@link HttpSecurity} object used to configure HTTP security.
     * @return a configured {@link SecurityFilterChain} object.
     */
    @Bean
    SecurityFilterChain filterChain(HttpSecurity httpSecurity, Environment env) {

        boolean isLocal = env.acceptsProfiles(Profiles.of("local", "testauth"));

        var authorizeHttpRequestsBuilder =
                new AuthorizeHttpRequestsBuilder(authManager, swaggerEnabled);

        var sessionManagementConfigurerBuilder =
                new SessionManagementConfigurerBuilder(concurrencyControlConfigurerCustomizer);

        var http = SecurityConfigSupport.applyCsrfConfig(
                        httpSecurity,
                        csrfTokenRepository
                )
                .addFilterAfter(SecurityConfigSupport.csrfCookieFilter(), CsrfFilter.class)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(authorizeHttpRequestsBuilder)
                .oauth2Login(oauth2 -> oauth2
                        .successHandler((_, response, _) -> response.sendRedirect("/")))
                .sessionManagement(sessionManagementConfigurerBuilder);

        return SecurityConfigSupport.applyCommonHeaders(http, isLocal, isLocal)
                .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        return SecurityConfigSupport.createCorsConfigurationSource(allowedCorsOrigin);
    }

}