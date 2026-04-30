package uk.gov.laa.gpfd.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static uk.gov.laa.gpfd.config.SecurityConfig.getContentSecurityPolicyConfig;

@Profile("test")
@TestConfiguration
public class TestSecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) {
        return http
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin())
                        .contentSecurityPolicy(csp -> getContentSecurityPolicyConfig(csp)
                                .reportOnly() // Included in local config for debugging purposes
                        )
                )
                .build();
    }
}
