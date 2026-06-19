package uk.gov.laa.gpfd.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import uk.gov.laa.gpfd.config.builders.AuthorizeHttpRequestsBuilder;
import uk.gov.laa.gpfd.config.builders.SessionManagementConfigurerBuilder;

import java.util.List;

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
@SuppressWarnings("java:S4502") // CSRF disabled only for H2 console — local/test profiles only, never active in prod
@Profile({"local", "test"})
@Configuration
@ConditionalOnProperty(name = "spring.cloud.azure.active-directory.enabled", havingValue = "false")
public class SecurityConfigLocal {

    private final CookieCsrfTokenRepository csrfTokenRepository;

    @Value("${gpfd.security.cors.allowed-origin:https://127.0.0.1:8080}")
    private String allowedCorsOrigin;

    public SecurityConfigLocal(CookieCsrfTokenRepository csrfTokenRepository) {
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
     */
    @Bean
    @Order(1)
    SecurityFilterChain staticChain(HttpSecurity http) {
        return SecurityConfigSupport.createStaticChain(http);
    }

    /**
     * Configures the {@link SecurityFilterChain} for the HTTP security settings.
     * <p>
     * This method customizes the security filter chain by applying the CSRF configuration,
     * enabling CORS, and applying common security headers. For local/test profiles,
     * authorization is simplified to permit all requests.
     * Static resources are configured using a separate filter chain to ensure
     * asset caching remains independent of authenticated response cache policies.
     * </p>
     *
     * @param httpSecurity the {@link HttpSecurity} object used to configure HTTP security.
     * @return a configured {@link SecurityFilterChain} object.
     */
    @Bean
    SecurityFilterChain filterChain(HttpSecurity httpSecurity) {

        var http = SecurityConfigSupport.applyCsrfConfig(
                        httpSecurity,
                        csrfTokenRepository
                )
                .addFilterAfter(SecurityConfigSupport.csrfCookieFilter(), CsrfFilter.class)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        return SecurityConfigSupport.applyCommonHeaders(http, true, true)
                .build();
    }

    /**
     * CORS configuration.
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        return SecurityConfigSupport.createCorsConfigurationSource(allowedCorsOrigin);
    }

    /**
     * Mock client registration repository for local/test profiles.
     */
    @Bean
    public ClientRegistrationRepository emptyClientRegistrationRepository() {

        ClientRegistration localRegistration =
                ClientRegistration.withRegistrationId("graph")
                        .clientId("mockClientId")
                        .clientSecret("mockClientSecret")
                        .scope("read")
                        .authorizationUri("test")
                        .redirectUri("test2")
                        .tokenUri("test3")
                        .authorizationGrantType(
                                AuthorizationGrantType.JWT_BEARER
                        )
                        .build();

        return new InMemoryClientRegistrationRepository(
                List.of(localRegistration)
        );
    }
}