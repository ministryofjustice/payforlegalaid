package uk.gov.laa.pfla.configuration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import uk.gov.laa.gpfd.config.builders.AuthorizeHttpRequestsBuilder;
import uk.gov.laa.gpfd.utils.SecurityUtils;

import java.util.List;
import java.util.UUID;

import static org.springframework.security.config.Customizer.withDefaults;

@TestConfiguration
@RequiredArgsConstructor
public class SecurityConfigTestSetup {
    private final AuthorizationManager<RequestAuthorizationContext> authManager;

    @Value("${swagger-ui.enabled:true}")
    private boolean swaggerEnabled;

    @Getter
    private static final UUID testUserOid = UUID.fromString("eec2e3c9-02d1-4013-920b-9531a01f89fd");

    @Bean
    @Primary
    @SneakyThrows
    public SecurityFilterChain filterChain(HttpSecurity http) {
        var authorizeHttpRequestsBuilder = new AuthorizeHttpRequestsBuilder(authManager, swaggerEnabled);
        http
                .authorizeHttpRequests(authorizeHttpRequestsBuilder)
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedPage("/oauth2/authorization/azure")
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .userDetailsService(userDetailsService())
                .httpBasic(withDefaults());
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        var testUser = User.withDefaultPasswordEncoder()
                .username("test-user")
                .password("test-password")
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(testUser);
    }

    @Bean
    @Primary
    public SecurityUtils securityUtils() {
        return new SecurityUtils() {
            @Override
            public List<String> extractRoles() {
                return RoleRegistry.getRoles();
            }

            @Override
            public UUID extractUserId() {
                return testUserOid;
            }
        };
    }

}