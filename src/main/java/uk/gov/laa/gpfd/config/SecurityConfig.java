package uk.gov.laa.gpfd.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;
import uk.gov.laa.gpfd.config.builders.AuthorizeHttpRequestsBuilder;
import uk.gov.laa.gpfd.config.builders.HttpSecuritySessionManagementConfigurerBuilder;
import uk.gov.laa.gpfd.config.builders.SessionManagementConfigurerBuilder;

import java.io.IOException;
import java.util.List;

import static com.azure.spring.cloud.autoconfigure.implementation.aad.security.AadWebApplicationHttpSecurityConfigurer.aadWebApplication;

/**
 * Configuration class to set up Spring Security for the application.
 */
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.cloud.azure.active-directory.enabled", havingValue = "true")
public class SecurityConfig {

    private final AuthorizationManager<RequestAuthorizationContext> authManager;
    private final HttpSecuritySessionManagementConfigurerBuilder concurrencyControlConfigurerCustomizer;
    private final CookieCsrfTokenRepository csrfTokenRepository;

    @Value("${gpfd.security.cors.allowed-origin:https://127.0.0.1:8080}")
    private String allowedCorsOrigin;

    /**
     * Dedicated filter chain for static resources.
     */
    @Bean
    @Order(1)
    SecurityFilterChain staticChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/govuk/**", "/moj/**", "/css/**", "/js/**", "/images/**")
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .headers(HeadersConfigurer::disable);
        return http.build();
    }

    /**
     * Main application security filter chain.
     */
    @Bean
    SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        var authorizeHttpRequestsBuilder =
                new AuthorizeHttpRequestsBuilder(authManager);

        var sessionManagementConfigurerBuilder =
                new SessionManagementConfigurerBuilder(concurrencyControlConfigurerCustomizer);

        XorCsrfTokenRequestAttributeHandler delegate =
                new XorCsrfTokenRequestAttributeHandler();

        delegate.setCsrfRequestAttributeName("_csrf");

        return httpSecurity
                .csrf(csrf -> csrf
                        .csrfTokenRepository(csrfTokenRepository)
                        .csrfTokenRequestHandler(delegate)
                        .ignoringRequestMatchers(
                                PathPatternRequestMatcher.withDefaults().matcher("/csp-report")
                        )
                )
                .addFilterAfter(csrfCookieFilter(), CsrfFilter.class)
                .with(aadWebApplication())
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(authorizeHttpRequestsBuilder)
                .sessionManagement(sessionManagementConfigurerBuilder)
                .headers(headers -> headers
                        .httpStrictTransportSecurity(hsts -> hsts
                                .maxAgeInSeconds(63072000)
                                .includeSubDomains(true)
                                .preload(true)
                        )
                        .contentTypeOptions(Customizer.withDefaults())
                        .referrerPolicy(referrerPolicy -> referrerPolicy
                                .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER)
                        )
                        .permissionsPolicyHeader(permissionsPolicy -> permissionsPolicy
                                .policy("interest-cohort=()")
                        )
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::deny)
                        .addHeaderWriter(new StaticHeadersWriter("Cache-Control", "no-store"))
                        .addHeaderWriter(new StaticHeadersWriter("Pragma", "no-cache"))
                        .contentSecurityPolicy(csp -> csp
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
                                )
                        )
                )
                .build();
    }

    /**
     * Forces Spring Security to generate and persist the CSRF token cookie.
     */
    @Bean
    OncePerRequestFilter csrfCookieFilter() {
        return new OncePerRequestFilter() {

            @Override
            protected void doFilterInternal(
                    @NonNull HttpServletRequest request,
                    @NonNull HttpServletResponse response,
                    @NonNull FilterChain filterChain
            ) throws ServletException, IOException {

                CsrfToken csrfToken =
                        (CsrfToken) request.getAttribute(CsrfToken.class.getName());

                if (csrfToken != null) {
                    csrfToken.getToken();
                }

                filterChain.doFilter(request, response);
            }
        };
    }

    /**
     * CORS configuration.
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(allowedCorsOrigin));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-XSRF-TOKEN"));

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}