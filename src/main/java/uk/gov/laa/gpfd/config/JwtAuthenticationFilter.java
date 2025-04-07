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

import static org.apache.commons.codec.digest.DigestUtils.sha256Hex;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final int TOKEN_PARTS = 3;
    private static final int TOKEN_ID_LENGTH = 8;

    private final JwtDecoder jwtDecoder;
    private final AppConfig appConfig;

    public JwtAuthenticationFilter(JwtDecoder jwtDecoder, AppConfig appConfig) {
        this.jwtDecoder = jwtDecoder;
        this.appConfig = appConfig;
    }

    @Override
    public void doFilterInternal(HttpServletRequest servletRequest, @NotNull HttpServletResponse servletResponse, @NotNull FilterChain filterChain) throws IOException, ServletException {
        var token = servletRequest.getHeader(TokenComponents.HEADER_TYPE.value);

        if (token != null && !token.isEmpty()) {
            String logIdentifier = sha256Hex(token).substring(0,TOKEN_ID_LENGTH);
            log.info("Token " + logIdentifier + " - token received, attempting validation");
            validateJwt(token, logIdentifier);
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    public boolean validateJwt(String token, String logIdentifier) {
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

            if (!decodedToken.getClaimAsString(TokenComponents.JWT_PAYLOAD_TENANT_ID_KEY.value).equals(appConfig.getEntraIdTenantId())) {
                throw new JwtException("Incorrect Tenant ID");
            }

            if (!decodedToken.getClaimAsString(TokenComponents.JWT_PAYLOAD_APPLICATION_ID_KEY.value).equals(appConfig.getEntraIdClientId())) {
                throw new JwtException("Incorrect Application ID");
            }

            if (isTokenExpired(decodedToken)) {
                throw new JwtException("Token is expired");
            }

            if (!isTokenValidForCurrentTime(decodedToken)) {
                throw new JwtException("Token not valid for current time");
            }

            if (!decodedToken.getClaimAsStringList(TokenComponents.SCOPE_KEY.value).contains(TokenComponents.SCOPE_VALUE.value)) {
                throw new JwtException("Expected scope values are missing");
            }

            log.info("Token " + logIdentifier + " - JWT validated successfully");

        } catch (JwtException ex) {
            throw new JwtException("Unable to validate token: " + ex.getMessage());
        } catch (Exception ex) {
            throw new JwtException("Unable to validate token.\n" + ex.getClass() + ": " + ex.getMessage());
        }

        return true;
    }

    public String extractJwtToken(String token) {
        final String INVALID_JWT_ERROR_MESSAGE = "Token is not a valid JWT";

        if (token == null || token.length() <= TokenComponents.TOKEN_PREFIX.value.length())
            throw new JwtException(INVALID_JWT_ERROR_MESSAGE);

        if (!token.substring(0, TokenComponents.TOKEN_PREFIX.value.length()).equalsIgnoreCase(TokenComponents.TOKEN_PREFIX.value))
            throw new JwtException(INVALID_JWT_ERROR_MESSAGE);

        token = token.substring(TokenComponents.TOKEN_PREFIX.value.length());

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

    enum TokenComponents {
        HEADER_TYPE("Authorization"),
        TOKEN_PREFIX("bearer "),
        JWT_PAYLOAD_TENANT_ID_KEY("tid"),
        JWT_PAYLOAD_APPLICATION_ID_KEY("appid"),
        SCOPE_KEY("scp"),
        SCOPE_VALUE("User.Read");

        public final String value;

        TokenComponents(String value) {
            this.value = value;
        }

    }
}