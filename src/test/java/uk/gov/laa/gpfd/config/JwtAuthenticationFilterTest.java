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
import org.springframework.security.oauth2.jwt.JwtDecoder;

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
    @SneakyThrows
    void shouldUseDefaultJourneyIfTokenHasContent() {
        when(mockRequest.getHeader("Authorization")).thenReturn("Bearer a.valid.token");
        jwtAuthenticationFilter.doFilter(mockRequest, mockResponse, mockFilterChain);
        verify(mockFilterChain).doFilter(mockRequest, mockResponse);
    }
}
