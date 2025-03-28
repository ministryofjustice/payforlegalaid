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
import org.junit.jupiter.params.provider.NullAndEmptySource;
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

import static org.junit.Assert.*;
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

    private final String expectedClientId = "clientId";
    private final String expectedTenantId = "tenantId";
    private final List<String> expectedScopes = List.of("User.Read");

    private Jwt jwt(String appId, Instant issuedAt, Instant expiresAt, List<String> scopes, String username) {

        Map<String, Object> claims = new java.util.HashMap<>(Map.of("sub", username,
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
            expectedScopes, "TestUser");


    @BeforeEach
    void beforeEach() {
        reset(mockRequest, mockRequest, mockFilterChain, jwtDecoder, appConfig);
        SecurityContextHolder.setContext(securityContext);
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtDecoder);
    }

    @ParameterizedTest
    @SneakyThrows
    @NullAndEmptySource
    void shouldCallFilterWhenNoTokenProvided(String token) {
        when(mockRequest.getHeader("Authorization")).thenReturn(token);
        jwtAuthenticationFilter.doFilter(mockRequest, mockResponse, mockFilterChain);
        verify(mockFilterChain).doFilter(mockRequest, mockResponse);
    }

    @ParameterizedTest
    @SneakyThrows
    @NullAndEmptySource
    void shouldCallFilterWhenValidTokenProvided(String token) {
        //TODO
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

    @Test
    void shouldThrowIfTokenInvalid() {
        when(mockRequest.getHeader("Authorization")).thenReturn("aaaa.bbbb.cccc");
        Exception ex = assertThrows(JwtException.class, () -> jwtAuthenticationFilter.doFilter(mockRequest, mockResponse, mockFilterChain));

        assertEquals("Token is not a valid JWT", ex.getMessage());
    }

    @Test
    void shouldThrowIfDecodeJwtThrows() {
        when(mockRequest.getHeader("Authorization")).thenReturn("Bearer aaaa.bbbb.cccc");
        when(jwtDecoder.decode("aaaa.bbbb.cccc")).thenThrow(new IllegalArgumentException());
        Exception ex = assertThrows(JwtException.class, () -> jwtAuthenticationFilter.doFilter(mockRequest, mockResponse, mockFilterChain));

        assertTrue( ex.getMessage().contains("Unable to validate token"));
    }

    @Test
    void shouldThrowIfValidateJwtThrows() {
        when(mockRequest.getHeader("Authorization")).thenReturn("Bearer aaaa.bbbb.cccc");
        when(jwtDecoder.decode("aaaa.bbbb.cccc")).thenReturn(null);
        Exception ex = assertThrows(JwtException.class, () -> jwtAuthenticationFilter.doFilter(mockRequest, mockResponse, mockFilterChain));

        assertEquals("Unable to validate token: decode token returned null", ex.getMessage());
    }

    @Test
    void shouldThrowIfDecodeJwtReturnsNull() {
        var token = "aaaa.bbbb.cccc";
        when(jwtDecoder.decode(token)).thenReturn(null);
        Exception ex = assertThrows(JwtException.class, () -> jwtAuthenticationFilter.validateJwt("Bearer " + token));

        assertEquals("Unable to validate token: decode token returned null", ex.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldThrowIfUsernameIsInvalid(String username) {
        var token = "aaaa.bbbb.cccc";
        when(jwtDecoder.decode(token)).thenReturn(jwt(expectedClientId,
                Instant.now(),
                Instant.now().plusSeconds(100),
                expectedScopes, username));
        Exception ex = assertThrows(JwtException.class, () -> jwtAuthenticationFilter.validateJwt("Bearer " + token));

        assertEquals("Unable to validate token: token includes no valid username", ex.getMessage());
    }
}
