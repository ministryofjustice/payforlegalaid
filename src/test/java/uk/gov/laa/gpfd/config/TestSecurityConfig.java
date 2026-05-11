package uk.gov.laa.gpfd.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

import static uk.gov.laa.gpfd.config.SecurityConfig.getContentSecurityPolicyConfig;

@Profile("test")
@SuppressWarnings("java:S4502") // CSRF disabled only for CSP report POST endpoint
@TestConfiguration
public class TestSecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) {
        return http
                .csrf(csrf -> csrf.ignoringRequestMatchers(
                        PathPatternRequestMatcher.withDefaults().matcher("/csp-report")
                ))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                        .contentSecurityPolicy(csp -> getContentSecurityPolicyConfig(csp)
                                .reportOnly() // Included in local config for debugging purposes
                        )
                )
                .build();
    }
}
