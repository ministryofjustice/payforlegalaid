package uk.gov.laa.gpfd.config;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
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
@Profile("!test")
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthorizationManager<RequestAuthorizationContext> authManager;
    private final HttpSecuritySessionManagementConfigurerBuilder concurrencyControlConfigurerCustomizer;

    /**
     * Builds and configures the Spring Security filter chain.
     *
     * <p>This method:
     * <ul>
     *     <li>Applies custom authorization rules</li>
     *     <li>Enables OAuth2 login and redirects to "/" on successful authentication</li>
     *     <li>Configures session management using the provided builder</li>
     * </ul>
     *
     * @param httpSecurity the {@link HttpSecurity} to configure
     * @return the configured {@link SecurityFilterChain}
     * @throws Exception if the security configuration fails
     */
    @Bean
    SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        var authorizeHttpRequestsBuilder = new AuthorizeHttpRequestsBuilder(authManager);
        var sessionManagementConfigurerBuilder = new SessionManagementConfigurerBuilder(concurrencyControlConfigurerCustomizer);
        return httpSecurity
                // Allow csp-report to ignore CSRF or else POST requests will be blocked
                .csrf(csrf -> csrf.ignoringRequestMatchers(
                        PathPatternRequestMatcher.withDefaults().matcher("/csp-report")
                ))
                .authorizeHttpRequests(authorizeHttpRequestsBuilder)
                .oauth2Login(oauth2 -> oauth2
                        .successHandler((request, response, authentication) -> {
                            response.sendRedirect("/");
                        }))
                .sessionManagement(sessionManagementConfigurerBuilder)
                .headers(headers -> headers
                        .contentSecurityPolicy(SecurityConfig::getContentSecurityPolicyConfig)
                )
                .build();
    }

    static HeadersConfigurer<HttpSecurity>.@NonNull ContentSecurityPolicyConfig getContentSecurityPolicyConfig(HeadersConfigurer<HttpSecurity>.ContentSecurityPolicyConfig csp) {
        return csp
                .policyDirectives(
                                "default-src 'none'; " +
                                "base-uri 'self'; " +
                                "object-src 'none'; " +
                                "frame-ancestors 'none'; " +
                                "form-action 'self'; " +
                                "script-src 'self'; " +
                                "style-src 'self'; " +
                                "img-src 'self' data:; " +
                                "font-src 'self'; " +
                                "connect-src 'self'; " +
                                "upgrade-insecure-requests; " +
                                "report-uri /csp-report"
                );
    }
}