package uk.gov.laa.gpfd.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String TOKEN_PREFIX = "bearer ";
    private static final int TOKEN_PARTS = 3;
    private static final String JWT_PAYLOAD_TENANT_ID_KEY = "tid";
    private static final String JWT_PAYLOAD_APPLICATION_ID_KEY = "appid";
    private static final String SCOPE_KEY = "scp";
    private static final String SCOPE_VALUE = "User.Read";

    private final JwtDecoder jwtDecoder;
    private final AppConfig appConfig;

    public JwtAuthenticationFilter(JwtDecoder jwtDecoder, AppConfig appConfig) {
        this.jwtDecoder = jwtDecoder;
        this.appConfig = appConfig;
    }

    @Override
    public void doFilterInternal(HttpServletRequest servletRequest, @NotNull HttpServletResponse servletResponse, @NotNull FilterChain filterChain) throws IOException, ServletException {
        var token = servletRequest.getHeader("Authorization");

        if (token == null || token.isEmpty()) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            validateJwt(token);
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    public boolean validateJwt(String token) {
        var jwtContent = extractJwtToken(token);

        try {
            Jwt decodedToken = jwtDecoder.decode(jwtContent);

            if (decodedToken == null)
                throw new JwtException("decode token returned null");

            String username = decodedToken.getSubject();

            if (username == null || username.isEmpty())
                throw new JwtException("token includes no valid username");

            if (!decodedToken.getAudience().contains(appConfig.getEntraIdClientId())) {
                throw new JwtException("Audience mismatch");
            }

            if (!decodedToken.getClaimAsString(JWT_PAYLOAD_TENANT_ID_KEY).equals(appConfig.getEntraIdTenantId())) {
                throw new JwtException("Incorrect Tenant ID");
            }

            if (!decodedToken.getClaimAsString(JWT_PAYLOAD_APPLICATION_ID_KEY).equals(appConfig.getEntraIdClientId())) {
                throw new JwtException("Incorrect Application ID");
            }

            if (!isTokenValidForCurrentTime(decodedToken)) {
                throw new JwtException("Token not valid for current time");
            }

            if (isTokenExpired(decodedToken)) {
                throw new JwtException("Token is expired");
            }

            if (!decodedToken.getClaimAsStringList(SCOPE_KEY).contains(SCOPE_VALUE)) {
                throw new JwtException("Expected scope values are missing");
            }

        } catch (JwtException ex) {
            throw new JwtException("Unable to validate token: " + ex.getMessage());
        } catch (Exception ex) {
            throw new JwtException("Unable to validate token");
        }

        return true;
    }

    public String extractJwtToken(String token) {
        final String INVALID_JWT_ERROR_MESSAGE = "Token is not a valid JWT";

        if (token == null || token.length() <= TOKEN_PREFIX.length())
            throw new JwtException(INVALID_JWT_ERROR_MESSAGE);

        if (!token.substring(0, TOKEN_PREFIX.length()).equalsIgnoreCase(TOKEN_PREFIX))
            throw new JwtException(INVALID_JWT_ERROR_MESSAGE);

        token = token.substring(TOKEN_PREFIX.length());

        var contents = token.split("\\.");

        if (contents.length != TOKEN_PARTS) {
            throw new JwtException(INVALID_JWT_ERROR_MESSAGE);
        }

        for (String s : contents) {
            if (s.isEmpty()) {
                throw new JwtException(INVALID_JWT_ERROR_MESSAGE);
            }
        }

        return token;
    }

    public boolean isTokenValidForCurrentTime(Jwt decodedToken) {
        try {
            return !decodedToken.getNotBefore().isAfter(Instant.now());
        } catch (NullPointerException ex) {
            throw new JwtException("Token not before time is null");
        }
    }

    public boolean isTokenExpired(Jwt decodedToken) {
        try {
            return decodedToken.getExpiresAt().isBefore(Instant.now());
        } catch (NullPointerException ex) {
            throw new JwtException("Token expiry is null");
        }
    }
}