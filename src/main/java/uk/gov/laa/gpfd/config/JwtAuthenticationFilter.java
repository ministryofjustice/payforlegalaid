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
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String TOKEN_PREFIX = "bearer ";
    private static final int TOKEN_PARTS = 3;
    private final JwtDecoder jwtDecoder;

    public JwtAuthenticationFilter(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
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

        //TODO - logs, hash token for logs, validation

        try {
            Jwt decodedToken = jwtDecoder.decode(jwtContent);

            if (decodedToken == null)
                throw new JwtException("decode token returned null");

            String username = decodedToken.getSubject();

            if (username == null || username.isEmpty())
                throw new JwtException("token includes no valid username");

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
