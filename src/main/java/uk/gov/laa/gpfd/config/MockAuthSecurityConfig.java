package uk.gov.laa.gpfd.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.web.filter.OncePerRequestFilter;
import uk.gov.laa.gpfd.security.SilasRoles;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Configuration(proxyBeanMethods = false)
@Profile("mockauth")
public class MockAuthSecurityConfig {

    private final MockAuthAccessPolicy accessPolicy;

    public MockAuthSecurityConfig(MockAuthAccessPolicy accessPolicy) {
        this.accessPolicy = accessPolicy;
    }

    MockAuthAccessPolicy accessPolicy() {
        return accessPolicy;
    }

    void configure(HttpSecurity http) {
        if (!accessPolicy.allows()) {
            http.authorizeHttpRequests(authorize -> authorize.requestMatchers("/login").denyAll())
                    .formLogin(form -> form.disable());
            return;
        }

        http.formLogin(form -> form.disable())
                .addFilterBefore(mockAuthenticationFilter(), AuthorizationFilter.class);
    }

    private OncePerRequestFilter mockAuthenticationFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain filterChain) throws ServletException, IOException {
                var context = SecurityContextHolder.getContext();
                if (context.getAuthentication() == null
                        || context.getAuthentication() instanceof AnonymousAuthenticationToken) {
                    context.setAuthentication(createAuthentication());
                }
                filterChain.doFilter(request, response);
            }
        };
    }

    private OAuth2AuthenticationToken createAuthentication() {
        var userId = UUID.randomUUID().toString();
        var now = Instant.now();
        var token = new OidcIdToken("mockauth", now, now.plusSeconds(3600), Map.of(
                "sub", userId,
                "oid", userId,
                "name", "Ephemeral test user",
                "LAA_APP_ROLES", SilasRoles.all()));
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        var principal = new DefaultOidcUser(authorities, token);
        return new OAuth2AuthenticationToken(principal, authorities, "mockauth");
    }
}