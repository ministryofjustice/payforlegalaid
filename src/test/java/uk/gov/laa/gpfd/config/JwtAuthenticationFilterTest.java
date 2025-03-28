package uk.gov.laa.gpfd.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

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
    @SneakyThrows
    void shouldUseDefaultJourneyIfTokenHasContent() {
        when(mockRequest.getHeader("Authorization")).thenReturn("Bearer a.valid.token");
        jwtAuthenticationFilter.doFilter(mockRequest, mockResponse, mockFilterChain);
        verify(mockFilterChain).doFilter(mockRequest, mockResponse);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Bearer ", "bearer ", "BEARER "})
    void shouldReturnJwtForValidBearerToken(String tokenPrefix) {
        assertEquals("aaaa.bbbb.cccc", jwtAuthenticationFilter.extractJwtToken(tokenPrefix + "aaaa.bbbb.cccc"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"ThisIsNotValid", "aaaa.bbbb.cccc", "Bearer ThisIsNotValidEither", "Bearer aaaa.", "Bearer .bbbb.cccc", "Bearer aaaaa..cccc", "Bearer aaaaa.bbbb.", "Bea", "Bearer "})
    void shouldThrowIfTokenNotRightFormat(String token) {
        Exception ex = assertThrows(JwtException.class, () -> jwtAuthenticationFilter.extractJwtToken(token));
        assertEquals("Token is not a valid JWT", ex.getMessage());
    }
}
