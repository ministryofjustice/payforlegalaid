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