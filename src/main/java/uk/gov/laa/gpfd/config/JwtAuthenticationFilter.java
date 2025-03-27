package uk.gov.laa.gpfd.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Locale;

@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtDecoder jwtDecoder;
    private final AppConfig appConfig;

    public JwtAuthenticationFilter(JwtDecoder jwtDecoder, AppConfig appConfig) {
        this.jwtDecoder = jwtDecoder;
        this.appConfig = appConfig;
    }

    /**
     *
     * @param servletRequest
     * @param servletResponse
     * @param filterChain
     * @throws IOException
     * @throws ServletException
     * Custom implementation to validate JWT when user logs in to Entra ID.
     * Creates authentication object and sets in the SecurityContext, in place of default session id behaviour.
     * Will default to using session id, as part of standard spring security flow, if invalid JWT is provided; prompting user to login.
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

        String token = extractJwtToken(httpServletRequest);

        if (token != null) {
            try {
                log.info("JWT token received, attempting validation");
                // Decode the JWT
                // This should also be validating the issuer and signature
                Jwt decodedToken = jwtDecoder.decode(token);
                String username = decodedToken.getSubject();

                if (!decodedToken.getAudience().contains(appConfig.getEntraIdClientId())) {
                    throw new JwtException("Audience mismatch");
                }

                if (!decodedToken.getClaimAsString("tid").equals(appConfig.getEntraIdTenantId())) {
                    throw new JwtException("Tenant ID mismatch");
                }

                if (!decodedToken.getClaimAsString("appid").equals(appConfig.getEntraIdClientId())) {
                    throw new JwtException("Application Id mismatch");
                }

                if (isTokenExpired(decodedToken)) {
                    throw new JwtException("Token has expired");
                }

                if (isTokenFromTheFuture(decodedToken)) {
                    throw new JwtException("Token is not yet valid");
                }

                if (!decodedToken.getClaimAsStringList("scp").contains("User.Read")) {
                    throw new JwtException("Expected scope values are missing");
                }

                log.info("JWT validated successfully");

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (JwtException e) {
                log.warn("Invalid JWT token", e);
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    private static boolean isTokenFromTheFuture(Jwt decodedToken) {
        return decodedToken.getNotBefore() != null && decodedToken.getNotBefore().isAfter(Instant.now());
    }

    private static boolean isTokenExpired(Jwt decodedToken) {
        return decodedToken.getExpiresAt() != null && decodedToken.getExpiresAt().isBefore(Instant.now());
    }

    private String extractJwtToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");

        if (header != null && header.substring(0,"bearer ".length()).toLowerCase(Locale.ROOT).equals("bearer ")) {
            log.debug("Have extracted JWT token from auth header");
            return header.substring("bearer ".length());
        } else {
            //No auth token, don't bother trying to JWT authenticate
            log.debug("No JWT token in auth header");
            return null;
        }
    }
}
