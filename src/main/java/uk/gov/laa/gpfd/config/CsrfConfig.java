package uk.gov.laa.gpfd.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

/**
 * Shared CSRF token repository configuration, common to all Spring profiles.
 */
@Configuration
public class CsrfConfig {

    @Bean
    @SuppressWarnings("java:S3330") // HttpOnly must be disabled so client-side JS can read the CSRF token and echo it back in a request header
    CookieCsrfTokenRepository csrfTokenRepository() {
        CookieCsrfTokenRepository repository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        repository.setCookieCustomizer(cookie -> {
            cookie.sameSite("Strict");
            cookie.secure(true);
            cookie.path("/");
            cookie.domain(null);
        });
        return repository;
    }
}