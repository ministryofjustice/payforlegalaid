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

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String TOKEN_PREFIX = "bearer ";
    private static final int TOKEN_PARTS = 3;
    private final JwtDecoder jwtDecoder;
    private final AppConfig appConfig;
    private static final String JWT_PAYLOAD_TENANT_ID_KEY = "tid";
    private static final String JWT_PAYLOAD_APPLICATION_ID_KEY = "appid";

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
        var jwtContent = "";
        var valid = false;

        jwtContent = extractJwtToken(token);

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

        } catch (JwtException ex) {
            throw new JwtException("Unable to validate token: " + ex.getMessage());
        } catch (Exception ex) {
            throw new JwtException("Unable to validate token");
        }

        valid = true;

        return valid;
    }

    public String extractJwtToken(String token) {
        String errorMessage = "Token is not a valid JWT";

        if (token == null || token.length() <= TOKEN_PREFIX.length())
            throw new JwtException(errorMessage);

        if (!token.substring(0, TOKEN_PREFIX.length()).equalsIgnoreCase(TOKEN_PREFIX))
            throw new JwtException(errorMessage);

        token = token.substring(TOKEN_PREFIX.length());

        var contents = token.split("\\.");

        if (contents.length != TOKEN_PARTS) {
            throw new JwtException(errorMessage);
        }

        for (String s : contents) {
            if (s.isEmpty()) {
                throw new JwtException(errorMessage);
            }
        }

        return token;
    }
}
