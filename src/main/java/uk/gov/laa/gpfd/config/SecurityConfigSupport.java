package uk.gov.laa.gpfd.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Shared helper methods for Spring Security configuration classes.
 */
@SuppressWarnings("java:S4502") // CSRF disabled only for H2 console — local/test profiles only, never active in prod
public final class SecurityConfigSupport {

    private SecurityConfigSupport() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static SecurityFilterChain createStaticChain(HttpSecurity http) {
        http.securityMatcher("/govuk/**", "/moj/**", "/css/**", "/js/**", "/images/**")
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .headers(HeadersConfigurer::disable);

        try {
            return http.build();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to build static security filter chain", e);
        }
    }

    public static HttpSecurity applyCsrfConfig(HttpSecurity http,
                                               CookieCsrfTokenRepository csrfTokenRepository) {
        XorCsrfTokenRequestAttributeHandler delegate = new XorCsrfTokenRequestAttributeHandler();
        delegate.setCsrfRequestAttributeName("_csrf");

        return http.csrf(csrf -> csrf
                .csrfTokenRepository(csrfTokenRepository)
                .csrfTokenRequestHandler(delegate)
        );
    }

    public static OncePerRequestFilter csrfCookieFilter() {
        return new OncePerRequestFilter() {

            @Override
            protected void doFilterInternal(
                    @NonNull HttpServletRequest request,
                    @NonNull HttpServletResponse response,
                    @NonNull FilterChain filterChain
            ) throws ServletException, IOException {
                CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
                if (csrfToken != null) {
                    csrfToken.getToken();
                }
                filterChain.doFilter(request, response);
            }
        };
    }

    public static HttpSecurity applyCommonHeaders(HttpSecurity http,
                                                  boolean allowSameOriginFrames,
                                                  boolean reportOnlyCsp) {
        return http.headers(headers -> {
            headers.httpStrictTransportSecurity(hsts -> hsts
                            .maxAgeInSeconds(63072000)
                            .includeSubDomains(true)
                            .preload(true)
                    )
                    .contentTypeOptions(Customizer.withDefaults())
                    .referrerPolicy(referrerPolicy -> referrerPolicy
                            .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER)
                    ).permissionsPolicyHeader(permissionsPolicy -> permissionsPolicy
                            .policy("interest-cohort=()")
                    )
                    // Allow h2-console to display in web-frames in local config
                    .frameOptions(allowSameOriginFrames
                            ? HeadersConfigurer.FrameOptionsConfig::sameOrigin
                            : HeadersConfigurer.FrameOptionsConfig::deny
                    )
                    .addHeaderWriter(new StaticHeadersWriter("Cache-Control", "no-store"))
                    .addHeaderWriter(new StaticHeadersWriter("Pragma", "no-cache"))
                    .contentSecurityPolicy(csp -> {
                        var policy = csp.policyDirectives(getContentSecurityPolicy());
                        if (reportOnlyCsp) {
                            // Included in local config for debugging purposes
                            policy.reportOnly();
                        }
                    });
        });
    }

    public static CorsConfigurationSource createCorsConfigurationSource(String allowedCorsOrigin) {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(allowedCorsOrigin));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-XSRF-TOKEN"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    public static String getContentSecurityPolicy() {
        return "default-src 'none'; " +
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
                "report-uri /csp-report";
    }

    public static PathPatternRequestMatcher createMatcher(String pattern) {
        return PathPatternRequestMatcher.withDefaults().matcher(pattern);
    }

    /**
     * Configures a dedicated security filter chain for Content Security Policy (CSP)
     * violation reports.
     *
     * <p>
     * Browsers submit CSP reports as unauthenticated POST requests to the reporting
     * endpoint when policy violations are detected. As these reports are generated by
     * the browser and do not represent user-initiated state-changing actions, CSRF
     * protection is not applicable.
     * </p>
     *
     * <p>
     * The endpoint is isolated into a separate filter chain with CSRF protection
     * disabled rather than using request matcher exclusions in the primary security
     * configuration. This ensures that CSRF protection remains enabled for all
     * application endpoints while allowing CSP reports to be received successfully.
     * </p>
     *
     * @param http the {@link HttpSecurity} object used to configure security for CSP
     *             report requests
     * @return a configured {@link SecurityFilterChain} for the CSP reporting endpoint
     */
    public static SecurityFilterChain createCspReportChain(HttpSecurity http) {

        http.securityMatcher("/csp-report")
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        return http.build();
    }
}
