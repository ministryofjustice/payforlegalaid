package uk.gov.laa.gpfd.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private AppConfig appConfig;

    @Mock
    private JwtDecoder jwtDecoder;

    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private HttpServletResponse mockResponse;

    @Mock
    private FilterChain mockFilterChain;

    @Mock
    private SecurityContext securityContext;

    @BeforeEach
    void beforeEach() {
        reset(mockRequest, mockRequest, mockFilterChain, jwtDecoder, appConfig);
        SecurityContextHolder.setContext(securityContext);
        jwtAuthenticationFilter = new JwtAuthenticationFilter();

    }

    @Test
    @SneakyThrows
    void shouldUseDefaultJourneyIfNullToken() {
        when(mockRequest.getHeader("Authorization")).thenReturn(null);
        jwtAuthenticationFilter.doFilter(mockRequest, mockResponse, mockFilterChain);
        verify(mockFilterChain).doFilter(mockRequest, mockResponse);
    }

    @Test
    @SneakyThrows
    void shouldUseDefaultJourneyIfEmptyToken() {
        when(mockRequest.getHeader("Authorization")).thenReturn("");
        jwtAuthenticationFilter.doFilter(mockRequest, mockResponse, mockFilterChain);
        verify(mockFilterChain).doFilter(mockRequest, mockResponse);
    }

    @Test
    void shouldReturnJwtForValidBearerToken() {
        assertEquals("aaaa.bbbb.cccc", jwtAuthenticationFilter.extractJwtToken("Bearer aaaa.bbbb.cccc"));
    }

    @Test
    void shouldReturnJwtForValidBearerTokenIgnoreCase() {
        assertEquals("aaaa.bbbb.cccc", jwtAuthenticationFilter.extractJwtToken("BEARER aaaa.bbbb.cccc"));

    }

    @Test
    void shouldThrowIfTokenNotRightFormat() {
        Exception ex = assertThrows(JwtException.class, () -> jwtAuthenticationFilter.extractJwtToken("ThisIsNotValid"));
        assertEquals("Token is not a valid JWT", ex.getMessage());
    }

    @Test
    void shouldThrowIfTokenMissingBearer() {
        Exception ex = assertThrows(JwtException.class, () -> jwtAuthenticationFilter.extractJwtToken("aaaa.bbbb.cccc"));
        assertEquals("Token is not a valid JWT", ex.getMessage());
    }

    @Test
    void shouldThrowIfTokenNoComponents() {
        Exception ex = assertThrows(JwtException.class, () -> jwtAuthenticationFilter.extractJwtToken("Bearer ThisIsNotValidEither"));
        assertEquals("Token is not a valid JWT", ex.getMessage());
    }

    @Test
    void shouldThrowIfTokenMissingComponent() {
        Exception ex = assertThrows(JwtException.class, () -> jwtAuthenticationFilter.extractJwtToken("Bearer aaaa."));
        assertEquals("Token is not a valid JWT", ex.getMessage());
    }

    @Test
    void shouldThrowIfTokenEmptyMetaData() {
        Exception ex = assertThrows(JwtException.class, () -> jwtAuthenticationFilter.extractJwtToken("Bearer .bbbb.cccc"));
        assertEquals("Token is not a valid JWT", ex.getMessage());
    }

    @Test
    void shouldThrowIfTokenEmptyPayload() {
        Exception ex = assertThrows(JwtException.class, () -> jwtAuthenticationFilter.extractJwtToken("Bearer aaaaa..cccc"));
        assertEquals("Token is not a valid JWT", ex.getMessage());
    }

    @Test
    void shouldThrowIfTokenEmptySignature() {
        Exception ex = assertThrows(JwtException.class, () -> jwtAuthenticationFilter.extractJwtToken("Bearer aaaaa.bbbb."));
        assertEquals("Token is not a valid JWT", ex.getMessage());
    }

    @Test
    void shouldThrowIfTokenShort() {
        Exception ex = assertThrows(JwtException.class, () -> jwtAuthenticationFilter.extractJwtToken("Bea"));
        assertEquals("Token is not a valid JWT", ex.getMessage());
    }

    @Test
    void shouldThrowIfNoContent() {
        Exception ex = assertThrows(JwtException.class, () -> jwtAuthenticationFilter.extractJwtToken("Bearer "));
        assertEquals("Token is not a valid JWT", ex.getMessage());
    }
}
