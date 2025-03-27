package uk.gov.laa.gpfd.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.JwtException;

import java.io.IOException;
import java.util.Locale;

@Slf4j
public class JwtAuthenticationFilter {

    private static final String TOKEN_PREFIX = "bearer ";
    private static final int TOKEN_PARTS = 3;

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        var token = httpServletRequest.getHeader("Authorization");

        if (token == null || token.isEmpty()) {
            filterChain.doFilter(servletRequest, servletResponse);
        }
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
