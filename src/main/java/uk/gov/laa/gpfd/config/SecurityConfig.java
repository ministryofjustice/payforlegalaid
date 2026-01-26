package uk.gov.laa.gpfd.config;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import uk.gov.laa.gpfd.config.builders.AuthorizeHttpRequestsBuilder;
import uk.gov.laa.gpfd.config.builders.SessionManagementConfigurerBuilder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.stream.Collectors;

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

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    /**
     * The custom {@link AuthorizeHttpRequestsBuilder} responsible for configuring
     * the authorization rules for HTTP requests, such as which endpoints are publicly
     * accessible and which require authentication.
     * This dependency is injected via constructor injection due to the
     * {@link RequiredArgsConstructor} annotation.
     */
   // private final AuthorizeHttpRequestsBuilder authorizeHttpRequestsBuilder;

    /**
     * The custom {@link SessionManagementConfigurerBuilder} responsible for configuring
     * session management, including session concurrency control and session expiration.
     * This dependency is injected via constructor injection due to the
     * {@link RequiredArgsConstructor} annotation.
     */
    private final SessionManagementConfigurerBuilder sessionManagementConfigurerBuilder;
    public static final String[] PUBLIC_PATHS = {
            "/actuator/**",
            "/logout",
            "/logout-success",
            "/css/**",
            "/assets/**",
            "/webjars/**",
            "/favicon.ico"
    };

    @PostConstruct public void init() {
        log.info(">>> SecurityConfig @PostConstruct executed");
    }

    /**
     * Configures the {@link SecurityFilterChain} for the HTTP security settings.
     * <p>
     * This method customizes the security filter chain by applying the authorization
     * rules, enabling HTTP basic authentication, and applying the session management
     * configuration to control session concurrency and expiration.
     * </p>
     *
     * @param httpSecurity the {@link HttpSecurity} object used to configure HTTP security.
     * @return a configured {@link SecurityFilterChain} object.
     * @throws Exception if any error occurs during the configuration of HTTP security.
     */
    @Bean
    SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        log.info(">>> Building SecurityFilterChain from SecurityConfig");

        return httpSecurity
                .authorizeHttpRequests(auth -> auth .requestMatchers(PUBLIC_PATHS).permitAll()
                        .anyRequest().authenticated() )

                .oauth2Login(oauth2 -> oauth2 .userInfoEndpoint(userInfo -> userInfo.oidcUserService(oidcUserService()))
                        .successHandler((request, response, authentication) -> { response.sendRedirect("/"); }) )
                .sessionManagement(sessionManagementConfigurerBuilder)
                .build();
    }

    @Bean
    public OidcUserService oidcUserService() {
        return new OidcUserService() {
            @Override
            public OidcUser loadUser(OidcUserRequest userRequest) {
                OidcUser oidcUser = super.loadUser(userRequest);
                Set<GrantedAuthority> authorities = getAuthorities(oidcUser.getAttributes());
                return new DefaultOidcUser(authorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
            }
        };
    }

    public Set<GrantedAuthority> getAuthorities(Map<String, Object> attributes) {
        List<String> roles = parseRawRoles(attributes.get("LAA_APP_ROLES"));
        return new SimpleAuthorityMapper()
                .mapAuthorities(
                        roles.stream()
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList())
                );
    }

    private List<String> parseRawRoles(Object rawRoles) {
        if (rawRoles instanceof List<?> list) {
            return list.stream().map(Object::toString).toList();
        } else if (rawRoles instanceof String str) {
            return List.of(str.split(","));
        } else {
            return List.of();
        }
    }


}