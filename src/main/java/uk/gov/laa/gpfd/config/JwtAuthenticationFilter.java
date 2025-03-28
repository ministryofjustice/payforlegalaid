package uk.gov.laa.gpfd.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

import java.io.IOException;

@Slf4j
public class JwtAuthenticationFilter {

    private static final String TOKEN_PREFIX = "bearer ";
    private static final int TOKEN_PARTS = 3;
    private final JwtDecoder jwtDecoder;

    public JwtAuthenticationFilter(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    public void doFilter(HttpServletRequest servletRequest, HttpServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        var jwtContent = "";
        var token = servletRequest.getHeader("Authorization");

        if (token != null && !token.isEmpty()) {
            jwtContent = extractJwtToken(token);
        }

        //TODO - logs, hash token for logs, validation

        try {
            Jwt decodedToken = jwtDecoder.decode(jwtContent);

            if (decodedToken == null)
                throw new JwtException("decode token returned null");

        } catch (JwtException ex) {
            throw new JwtException("Unable to validate token: " + ex.getMessage());
        } catch (Exception ex) {
            throw new JwtException("Unable to validate token");
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
