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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    private final String expectedClientId = "clientId";
    private final String expectedTenantId = "tenantId";
    private final List<String> expectedScopes = List.of("User.Read");

    private Jwt jwt(String appId, Instant issuedAt, Instant expiresAt, List<String> scopes) {

        Map<String, Object> claims = new java.util.HashMap<>(Map.of("sub", "Chris",
                "aud", expectedClientId,
                "tid", expectedTenantId,
                "appid", appId,
                "scp", scopes
        ));

        if (issuedAt != null) {
            claims.put("nbf", issuedAt);
        }

        return new Jwt("tokenValue", issuedAt, expiresAt,
                Map.of("alg", "RSA28"),
                claims
        );
    }

    private final Jwt testJwt = jwt(expectedClientId,
            Instant.now(),
            Instant.now().plusSeconds(100),
            expectedScopes);


    @BeforeEach
    void beforeEach() {
        reset(mockRequest, mockRequest, mockFilterChain, jwtDecoder, appConfig);
        SecurityContextHolder.setContext(securityContext);
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtDecoder, appConfig);

    }

    @Test
    @SneakyThrows
    void shouldSetAuthenticationIfBearerTokenIsValid() {
        when(mockRequest.getHeader("Authorization")).thenReturn("Bearer tokenValue");
        when(appConfig.getEntraIdClientId()).thenReturn(expectedClientId);
        when(appConfig.getEntraIdTenantId()).thenReturn(expectedTenantId);
        when(jwtDecoder.decode("tokenValue")).thenReturn(testJwt);

        jwtAuthenticationFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(securityContext).setAuthentication(any());
        verify(mockFilterChain).doFilter(mockRequest, mockResponse);

    }

    @Test
    @SneakyThrows
    void shouldSetAuthenticationIfBearerTokenHasCapitals() {
        when(mockRequest.getHeader("Authorization")).thenReturn("BEARER tokenValue");
        when(appConfig.getEntraIdClientId()).thenReturn(expectedClientId);
        when(appConfig.getEntraIdTenantId()).thenReturn(expectedTenantId);
        when(jwtDecoder.decode("tokenValue")).thenReturn(testJwt);

        jwtAuthenticationFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(securityContext).setAuthentication(any());
        verify(mockFilterChain).doFilter(mockRequest, mockResponse);

    }

    @Test
    @SneakyThrows
    void shouldNotAuthoriseIfJwtDecoderThrowsError() {
        //E.g. invalid signature
        when(mockRequest.getHeader("Authorization")).thenReturn("Bearer tokenValue");
        when(jwtDecoder.decode("tokenValue")).thenThrow(new JwtException("Invalid Signature"));

        jwtAuthenticationFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(securityContext, times(0)).setAuthentication(any());
        verify(mockFilterChain).doFilter(mockRequest, mockResponse);

    }

    @Test
    @SneakyThrows
    void shouldNotAuthoriseIfAudienceMismatch() {
        when(mockRequest.getHeader("Authorization")).thenReturn("Bearer tokenValue");
        when(appConfig.getEntraIdClientId()).thenReturn("clientId2");

        when(jwtDecoder.decode("tokenValue")).thenReturn(testJwt);

        jwtAuthenticationFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(securityContext, times(0)).setAuthentication(any());
        verify(mockFilterChain).doFilter(mockRequest, mockResponse);

    }

    @Test
    @SneakyThrows
    void shouldNotAuthoriseIfTenantMismatch() {
        when(mockRequest.getHeader("Authorization")).thenReturn("Bearer tokenValue");
        when(appConfig.getEntraIdClientId()).thenReturn(expectedClientId);
        when(appConfig.getEntraIdTenantId()).thenReturn("tenantId2");

        when(jwtDecoder.decode("tokenValue")).thenReturn(testJwt);

        jwtAuthenticationFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(securityContext, times(0)).setAuthentication(any());
        verify(mockFilterChain).doFilter(mockRequest, mockResponse);

    }

    @Test
    @SneakyThrows
    void shouldNotAuthoriseIfAppIdMismatch() {
        when(mockRequest.getHeader("Authorization")).thenReturn("Bearer tokenValue");
        when(appConfig.getEntraIdClientId()).thenReturn(expectedClientId);
        when(appConfig.getEntraIdTenantId()).thenReturn(expectedTenantId);

        Jwt problemJwt = jwt("clientId2",
                Instant.now(),
                Instant.now().plusSeconds(100),
                expectedScopes);
        when(jwtDecoder.decode("tokenValue")).thenReturn(problemJwt);

        jwtAuthenticationFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(securityContext, times(0)).setAuthentication(any());
        verify(mockFilterChain).doFilter(mockRequest, mockResponse);

    }

    @Test
    @SneakyThrows
    void shouldNotAuthoriseIfNotBeforeTimeInFuture() {
        when(mockRequest.getHeader("Authorization")).thenReturn("Bearer tokenValue");
        when(appConfig.getEntraIdClientId()).thenReturn(expectedClientId);
        when(appConfig.getEntraIdTenantId()).thenReturn(expectedTenantId);

        Jwt problemJwt = jwt(expectedClientId,
                Instant.now().plusSeconds(100),
                Instant.now().plusSeconds(100),
                expectedScopes);
        when(jwtDecoder.decode("tokenValue")).thenReturn(problemJwt);

        jwtAuthenticationFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(securityContext, times(0)).setAuthentication(any());
        verify(mockFilterChain).doFilter(mockRequest, mockResponse);

    }

    @Test
    @SneakyThrows
    void shouldNotAuthoriseIfExpiryInPast() {
        when(mockRequest.getHeader("Authorization")).thenReturn("Bearer tokenValue");
        when(appConfig.getEntraIdClientId()).thenReturn(expectedClientId);
        when(appConfig.getEntraIdTenantId()).thenReturn(expectedTenantId);

        Jwt problemJwt = jwt(expectedClientId,
                Instant.now().minusSeconds(101),
                Instant.now().minusSeconds(100),
                expectedScopes);
        when(jwtDecoder.decode("tokenValue")).thenReturn(problemJwt);

        jwtAuthenticationFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(securityContext, times(0)).setAuthentication(any());
        verify(mockFilterChain).doFilter(mockRequest, mockResponse);

    }

    @Test
    @SneakyThrows
    void shouldNotAuthoriseIfMissingRequiredScopes() {
        when(mockRequest.getHeader("Authorization")).thenReturn("Bearer tokenValue");
        when(appConfig.getEntraIdClientId()).thenReturn(expectedClientId);
        when(appConfig.getEntraIdTenantId()).thenReturn(expectedTenantId);

        Jwt problemJwt = jwt(expectedClientId,
                Instant.now(),
                Instant.now().plusSeconds(100),
                List.of("something different"));
        when(jwtDecoder.decode("tokenValue")).thenReturn(problemJwt);

        jwtAuthenticationFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(securityContext, times(0)).setAuthentication(any());
        verify(mockFilterChain).doFilter(mockRequest, mockResponse);

    }

    @Test
    @SneakyThrows
    void shouldSetAuthenticationIfAllValidAndNoExpiryOnToken() {
        when(mockRequest.getHeader("Authorization")).thenReturn("Bearer tokenValue");
        when(appConfig.getEntraIdClientId()).thenReturn(expectedClientId);
        when(appConfig.getEntraIdTenantId()).thenReturn(expectedTenantId);

        Jwt noExpiryJwt = jwt(expectedClientId, null, null, expectedScopes);
        when(jwtDecoder.decode("tokenValue")).thenReturn(noExpiryJwt);

        jwtAuthenticationFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(securityContext).setAuthentication(any());
        verify(mockFilterChain).doFilter(mockRequest, mockResponse);

    }

    @Test
    @SneakyThrows
    void shouldNotAuthoriseIfNoAuthHeader() {

        jwtAuthenticationFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(securityContext, times(0)).setAuthentication(any());
        verify(mockFilterChain).doFilter(mockRequest, mockResponse);

    }

    @Test
    @SneakyThrows
    void shouldNotAuthoriseIfNotBearerToken() {

        when(mockRequest.getHeader("Authorization")).thenReturn("NotABearer sad :(");

        jwtAuthenticationFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(securityContext, times(0)).setAuthentication(any());
        verify(mockFilterChain).doFilter(mockRequest, mockResponse);

    }

}
