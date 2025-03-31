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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.reset;
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

    private static final String EXPECTED_CLIENT_ID = "clientId";
    private static final String EXPECTED_TENANT_ID = "tenantId";
    private static final List<String> EXPECTED_SCOPES = List.of("User.Read");
    private static final String VALID_USER = "TestUser";
    private static final String VALID_TOKEN = "aaaa.bbbb.cccc";
    private static final Instant PAST_TIMESTAMP = Instant.now().minusSeconds(500);
    private static final Instant PAST_EXPIRY_TIMESTAMP = PAST_TIMESTAMP.plusSeconds(10);
    private static final Instant FUTURE_TIMESTAMP = Instant.now().plusSeconds(500);
    private static final Instant FUTURE_EXPIRY_TIMESTAMP = FUTURE_TIMESTAMP.plusSeconds(10);



    private Jwt jwt(String appId, Instant notBefore, Instant expiresAt, List<String> scopes, String username) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", username);
        claims.put("nbf", notBefore);
        claims.put("appid", appId);

        claims.putAll(Map.of("aud", EXPECTED_CLIENT_ID,
                "tid", EXPECTED_TENANT_ID,
                "scp", scopes));

        return new Jwt("tokenValue", notBefore, expiresAt,
                Map.of("alg", "RSA28"),
                claims
        );
    }

    private final Jwt testJwt = jwt(EXPECTED_CLIENT_ID,
            Instant.now(),
            Instant.now().plusSeconds(100),
            EXPECTED_SCOPES, VALID_USER);


    @BeforeEach
    void beforeEach() {
        reset(mockRequest, mockRequest, mockFilterChain, jwtDecoder, appConfig);
        SecurityContextHolder.setContext(securityContext);
        jwtAuthenticationFilter = new JwtAuthenticationFilter();
    }

    @ParameterizedTest
    @SneakyThrows
    @NullAndEmptySource
    void shouldCallFilterWhenNoTokenProvided(String token) {
        when(mockRequest.getHeader("Authorization")).thenReturn(token);
        jwtAuthenticationFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain);
        verify(mockFilterChain).doFilter(mockRequest, mockResponse);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Bearer ", "bearer ", "BEARER "})
    void shouldReturnJwtForValidBearerToken(String tokenPrefix) {
        assertEquals(VALID_TOKEN, jwtAuthenticationFilter.extractJwtToken(tokenPrefix + VALID_TOKEN));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"ThisIsNotValid", "xxxx.yyyy.zzzz", "Bearer ThisIsNotValidEither", "Bearer aaaa.", "Bearer aaaaa.bbbbbbb.cccc.ddddddd", "Bearer .bbbb.cccc", "Bearer aaaaa..cccc", "Bearer aaaaa.bbbb.", "Bea", "Bearer "})
    void shouldThrowIfTokenNotRightFormat(String token) {
        Exception ex = assertThrows(JwtException.class, () -> jwtAuthenticationFilter.extractJwtToken(token));
        assertEquals("Token is not a valid JWT", ex.getMessage());
    }

    @Test
    @SneakyThrows
    void shouldReturnFalseIfBeforeNotBeforeTime() {
        Jwt decodedToken = jwt(EXPECTED_CLIENT_ID,
                FUTURE_TIMESTAMP, FUTURE_EXPIRY_TIMESTAMP,
                EXPECTED_SCOPES, VALID_USER);
        assertFalse(jwtAuthenticationFilter.isTokenValidForCurrentTime(decodedToken));
    }

    @Test
    @SneakyThrows
    void shouldReturnFalseIfNotBeforeTimeIsNull() {
        Jwt decodedToken = jwt(EXPECTED_CLIENT_ID,
                null,
                FUTURE_TIMESTAMP, EXPECTED_SCOPES, VALID_USER);
        assertFalse(jwtAuthenticationFilter.isTokenValidForCurrentTime(decodedToken));
    }

    @Test
    @SneakyThrows
    void shouldReturnTrueIfAfterNotBeforeTime() {
        Jwt decodedToken = jwt(EXPECTED_CLIENT_ID,
                Instant.now(),
                FUTURE_TIMESTAMP, EXPECTED_SCOPES, VALID_USER);
        assertTrue(jwtAuthenticationFilter.isTokenValidForCurrentTime(decodedToken));
    }

    @Test
    @SneakyThrows
    void shouldReturnTrueIfAfterExpiresTime() {
        Jwt decodedToken = jwt(EXPECTED_CLIENT_ID,
                PAST_TIMESTAMP, PAST_EXPIRY_TIMESTAMP,
                EXPECTED_SCOPES, VALID_USER);
        assertTrue(jwtAuthenticationFilter.isTokenExpired(decodedToken));
    }

    @Test
    @SneakyThrows
    void shouldReturnTrueIfExpiresTimeIsNull() {
        Jwt decodedToken = jwt(EXPECTED_CLIENT_ID,
                PAST_TIMESTAMP, null,
                EXPECTED_SCOPES, VALID_USER);
        assertTrue(jwtAuthenticationFilter.isTokenExpired(decodedToken));
    }

    @Test
    @SneakyThrows
    void shouldReturnFalseIfBeforeExpiresTime() {
        Jwt decodedToken = jwt(EXPECTED_CLIENT_ID,
                Instant.now(),
                FUTURE_TIMESTAMP, EXPECTED_SCOPES, VALID_USER);
        assertFalse(jwtAuthenticationFilter.isTokenExpired(decodedToken));
    }
}
