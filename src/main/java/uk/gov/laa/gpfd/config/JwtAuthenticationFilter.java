package uk.gov.laa.gpfd.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final int TOKEN_PARTS = 3;
    private static final Map<String, String> errorMessages = Map.of(
            JwtClaimNames.AUD, "Audience mismatch",
            JwtTokenComponents.TENANT_ID_KEY.value, "Incorrect Tenant ID",
            JwtTokenComponents.APPLICATION_ID_KEY.value,"Incorrect Application ID",
            JwtClaimNames.EXP, "Token is expired",
            JwtClaimNames.NBF, "Token not valid for current time",
            JwtTokenComponents.SCOPE_KEY.value, "Expected scope values are missing");

    private final JwtDecoder jwtDecoder;
    private final AppConfig appConfig;

    public JwtAuthenticationFilter(JwtDecoder jwtDecoder, AppConfig appConfig) {
        this.jwtDecoder = jwtDecoder;
        this.appConfig = appConfig;
    }

    @Override
    public void doFilterInternal(HttpServletRequest servletRequest, @NotNull HttpServletResponse servletResponse, @NotNull FilterChain filterChain) throws IOException, ServletException {
        var token = servletRequest.getHeader(JwtTokenComponents.HEADER_TYPE.value);

        if (token != null && !token.isEmpty()) {
            validateJwt(token);
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    private void validateJwt(String token) {
        var jwtContent = extractJwtToken(token);

        try {
            Jwt decodedToken = jwtDecoder.decode(jwtContent);

            if (decodedToken == null)
                throw new JwtException("decode token returned null");

            String username = decodedToken.getSubject();

            if (username == null || username.isEmpty())
                throw new JwtException("token includes no valid username");

            if (!decodedToken.getAudience().contains(appConfig.getEntraIdClientId())) {
                throw new JwtException(errorMessages.get(JwtClaimNames.AUD));
            }

            if (!decodedToken.getClaimAsString(JwtTokenComponents.TENANT_ID_KEY.value).equals(appConfig.getEntraIdTenantId())) {
                throw new JwtException(errorMessages.get(JwtTokenComponents.TENANT_ID_KEY.value));
            }

            if (!decodedToken.getClaimAsString(JwtTokenComponents.APPLICATION_ID_KEY.value).equals(appConfig.getEntraIdClientId())) {
                throw new JwtException(errorMessages.get(JwtTokenComponents.APPLICATION_ID_KEY.value));
            }

            if (isTokenExpired(decodedToken)) {
                throw new JwtException(errorMessages.get(JwtClaimNames.EXP));
            }

            if (!isTokenValidForCurrentTime(decodedToken)) {
                throw new JwtException(errorMessages.get(JwtClaimNames.NBF));
            }

            if (!decodedToken.getClaimAsStringList(JwtTokenComponents.SCOPE_KEY.value).contains(JwtTokenComponents.SCOPE_VALUE.value)) {
                throw new JwtException(errorMessages.get(JwtTokenComponents.SCOPE_KEY.value));
            }

        } catch (JwtException ex) {
            throw new JwtException("Unable to validate token: " + ex.getMessage());
        } catch (Exception ex) {
            throw new JwtException("Unable to validate token.\n" + ex.getClass() + ": " + ex.getMessage());
        }

    }

    private String extractJwtToken(String token) {
        final String INVALID_JWT_ERROR_MESSAGE = "Token is not a valid JWT";

        if (token == null || token.length() <= JwtTokenComponents.TOKEN_PREFIX.value.length())
            throw new JwtException(INVALID_JWT_ERROR_MESSAGE);

        if (!token.substring(0, JwtTokenComponents.TOKEN_PREFIX.value.length()).equalsIgnoreCase(JwtTokenComponents.TOKEN_PREFIX.value))
            throw new JwtException(INVALID_JWT_ERROR_MESSAGE);

        token = token.substring(JwtTokenComponents.TOKEN_PREFIX.value.length());

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

    private boolean isTokenValidForCurrentTime(Jwt decodedToken) {
        try {
            return !decodedToken.getNotBefore().isAfter(Instant.now());
        } catch (NullPointerException ex) {
            throw new JwtException("Token not before time is null");
        }
    }

    private boolean isTokenExpired(Jwt decodedToken) {
        try {
            return decodedToken.getExpiresAt().isBefore(Instant.now());
        } catch (NullPointerException ex) {
            throw new JwtException("Token expiry is null");
        }
    }
}