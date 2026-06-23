package uk.gov.laa.gpfd.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.internal.annotation.SuppressFBWarnings;
import org.jspecify.annotations.NonNull;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;

import static org.apache.commons.codec.digest.DigestUtils.sha256Hex;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final int TOKEN_ID_LENGTH = 8;
    private static final int TOKEN_PARTS = 3;
    private static final Map<String, String> errorMessages = Map.of(
            JwtClaimNames.AUD, "Audience mismatch",
            JwtTokenComponents.TENANT_ID_KEY.value, "Incorrect Tenant ID",
            JwtTokenComponents.APPLICATION_ID_KEY.value, "Incorrect Application ID",
            JwtClaimNames.EXP, "Token is expired",
            JwtClaimNames.NBF, "Token not valid for current time",
            JwtTokenComponents.SCOPE_KEY.value, "Expected scope values are missing");

    private final JwtDecoder jwtDecoder;
    private final AppConfig appConfig;

    public JwtAuthenticationFilter(JwtDecoder jwtDecoder, AppConfig appConfig) {
        this.jwtDecoder = jwtDecoder;
        this.appConfig = appConfig;
    }

    @SuppressFBWarnings(
            value = "SECSH",
            justification = "Token header is user-supplied by design as a JWT bearer token; it is hashed before logging and structurally validated before decode"
    )
    @Override
    public void doFilterInternal(HttpServletRequest servletRequest, @NonNull HttpServletResponse servletResponse, @NonNull FilterChain filterChain) throws IOException, ServletException {
        var token = servletRequest.getHeader(JwtTokenComponents.HEADER_TYPE.value);

        if (token != null && !token.isEmpty()) {
            String logIdentifier = sha256Hex(token).substring(0, TOKEN_ID_LENGTH);
            log.info("Token {} - token received, attempting validation", logIdentifier);
            validateJwt(token, logIdentifier);
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    private void validateJwt(String token, String logIdentifier) {
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

            Instant expiresAt = decodedToken.getExpiresAt();
            if (expiresAt == null) {
                throw new JwtException("Token expiry is null");
            }
            if (isTokenExpired(expiresAt)) {
                throw new JwtException(errorMessages.get(JwtClaimNames.EXP));
            }

            Instant notBefore = decodedToken.getNotBefore();
            if (notBefore == null) {
                throw new JwtException("Token not before time is null");
            }
            if (!isTokenValidForCurrentTime(notBefore)) {
                throw new JwtException(errorMessages.get(JwtClaimNames.NBF));
            }

            if (!decodedToken.getClaimAsStringList(JwtTokenComponents.SCOPE_KEY.value).contains(JwtTokenComponents.SCOPE_VALUE.value)) {
                throw new JwtException(errorMessages.get(JwtTokenComponents.SCOPE_KEY.value));
            }

            log.info("Token " + logIdentifier + " - JWT validated successfully");

        } catch (JwtException ex) {
            throw new JwtException("Unable to validate token: " + ex.getMessage());
        } catch (Exception ex) {
            throw new JwtException("Unable to validate token.\n" + ex.getClass() + ": " + ex.getMessage());
        }
    }

    @SuppressFBWarnings(
            value = "SECUNI",
            justification = "TOKEN_PREFIX contains only ASCII characters; locale-sensitive case folding cannot affect this comparison"
    )
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

    private boolean isTokenValidForCurrentTime(Instant notBefore) {
        return !notBefore.isAfter(Instant.now());
    }

    private boolean isTokenExpired(Instant expiresAt) {
        return expiresAt.isBefore(Instant.now());
    }
}
