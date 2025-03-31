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

    @Override
    public void doFilterInternal(HttpServletRequest servletRequest, @NotNull HttpServletResponse servletResponse, @NotNull FilterChain filterChain) throws IOException, ServletException {
        var token = servletRequest.getHeader("Authorization");

        if (token != null && !token.isEmpty()) {
            // JWT validation logic to come in later PR
        }

        filterChain.doFilter(servletRequest, servletResponse);

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
        } catch (Exception ex){
            return false;
        }

    }

    public boolean isTokenExpired(Jwt decodedToken) {
        try {
            return decodedToken.getExpiresAt().isBefore(Instant.now());
        } catch (Exception ex) {
            return true;
        }
    }
}