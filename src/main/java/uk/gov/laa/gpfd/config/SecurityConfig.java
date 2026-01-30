package uk.gov.laa.gpfd.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;
import uk.gov.laa.gpfd.config.builders.AuthorizeHttpRequestsBuilder;
import uk.gov.laa.gpfd.config.builders.SessionManagementConfigurerBuilder;

import java.util.List;
import java.util.Map;
import java.util.Set;
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
@Profile("!local & !test")
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    /**
     * The custom {@link AuthorizeHttpRequestsBuilder} responsible for configuring
     * the authorization rules for HTTP requests, such as which endpoints are publicly
     * accessible and which require authentication.
     * This dependency is injected via constructor injection due to the
     * {@link RequiredArgsConstructor} annotation.
     */
    private final AuthorizeHttpRequestsBuilder authorizeHttpRequestsBuilder;

    /**
     * The custom {@link SessionManagementConfigurerBuilder} responsible for configuring
     * session management, including session concurrency control and session expiration.
     * This dependency is injected via constructor injection due to the
     * {@link RequiredArgsConstructor} annotation.
     */
    private final SessionManagementConfigurerBuilder sessionManagementConfigurerBuilder;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .authorizeHttpRequests(authorizeHttpRequestsBuilder)
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.oidcUserService(oidcUserService()))
                        .successHandler((request, response, authentication) -> {
                            response.sendRedirect("/");
                        }))
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
        roles.forEach(role -> System.out.println("Role: " + role));
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