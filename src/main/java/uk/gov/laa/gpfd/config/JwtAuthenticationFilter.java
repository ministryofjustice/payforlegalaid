package uk.gov.laa.gpfd.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
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
        String errorMessage = "Token is not a valid JWT";

        if (token.length() <= TOKEN_PREFIX.length())
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
