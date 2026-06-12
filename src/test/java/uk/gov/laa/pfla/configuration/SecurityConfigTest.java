package uk.gov.laa.pfla.configuration;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@RequiredArgsConstructor
public class SecurityConfigTest {
    private final AuthorizationManager<RequestAuthorizationContext> authManager;

    @Bean
    @Primary
    @SneakyThrows
    public SecurityFilterChain filterChain(HttpSecurity http) {
        var authorizeHttpRequestsBuilder = new AuthorizeHttpRequestsBuilder(authManager);
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
}