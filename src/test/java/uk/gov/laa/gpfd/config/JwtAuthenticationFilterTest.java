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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
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

    private static final String EXPECTED_CLIENT_ID = "clientId";
    private static final String EXPECTED_TENANT_ID = "tenantId";
    private static final List<String> EXPECTED_SCOPES = List.of("User.Read");
    private static final String VALID_USER = "TestUser";
    private static final String VALID_BEARER_TOKEN = "Bearer aaaa.bbbb.cccc";
    private static final String VALID_TOKEN = "aaaa.bbbb.cccc";
    private static final Instant PAST_TIMESTAMP = Instant.now().minusSeconds(500);
    private static final Instant PAST_EXPIRY_TIMESTAMP = PAST_TIMESTAMP.plusSeconds(10);
    private static final Instant FUTURE_TIMESTAMP = Instant.now().plusSeconds(500);
    private static final Instant FUTURE_EXPIRY_TIMESTAMP = FUTURE_TIMESTAMP.plusSeconds(10);

    @BeforeEach
    void beforeEach() {
        reset(mockRequest, mockRequest, mockFilterChain, jwtDecoder, appConfig);
        SecurityContextHolder.setContext(securityContext);
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtDecoder, appConfig);
    }

    @ParameterizedTest
    @SneakyThrows
    @NullAndEmptySource
    void shouldCallFilterWhenNoTokenProvided(String token) {
        when(mockRequest.getHeader("Authorization")).thenReturn(token);
        jwtAuthenticationFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain);
        verify(mockFilterChain).doFilter(mockRequest, mockResponse);
        verify(jwtDecoder, times(0)).decode(any());
    }

    @SneakyThrows
    @Test
    void shouldCallFilterWhenValidTokenProvided() {
        when(jwtDecoder.decode(VALID_TOKEN)).thenReturn(jwt(EXPECTED_CLIENT_ID,
                Instant.now(),
                Instant.now().plusSeconds(100),
                EXPECTED_SCOPES, VALID_USER));

        when(appConfig.getEntraIdClientId()).thenReturn(EXPECTED_CLIENT_ID);
        when(appConfig.getEntraIdTenantId()).thenReturn(EXPECTED_TENANT_ID);
        when(appConfig.getScope()).thenReturn(EXPECTED_SCOPES);

        when(mockRequest.getHeader("Authorization")).thenReturn(VALID_BEARER_TOKEN);
        jwtAuthenticationFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain);
        verify(mockFilterChain).doFilter(mockRequest, mockResponse);
        verify(jwtDecoder, times(1)).decode(any());
    }

    @Test
    void shouldThrowWhenTokenInvalid() {
        when(mockRequest.getHeader("Authorization")).thenReturn("InvalidToken");
        Exception ex = assertThrows(JwtException.class, () -> jwtAuthenticationFilter.doFilter(mockRequest, mockResponse, mockFilterChain));

        assertEquals("Token is not a valid JWT", ex.getMessage());
    }

    @Test
    void shouldThrowWhenDecodeJwtThrows() {
        when(mockRequest.getHeader("Authorization")).thenReturn(VALID_BEARER_TOKEN);
        when(jwtDecoder.decode(any())).thenThrow(new IllegalArgumentException("decode has failed"));
        Exception ex = assertThrows(JwtException.class, () -> jwtAuthenticationFilter.doFilter(mockRequest, mockResponse, mockFilterChain));
        assertTrue(ex.getMessage().contains("Unable to validate token"));
        assertTrue(ex.getMessage().contains("IllegalArgumentException"));
        assertTrue(ex.getMessage().contains("decode has failed"));
    }

    @Test
    void shouldThrowWhenValidateJwtThrows() {
        when(mockRequest.getHeader("Authorization")).thenReturn(VALID_BEARER_TOKEN);
        when(jwtDecoder.decode("aaaa.bbbb.cccc")).thenReturn(null);
        Exception ex = assertThrows(JwtException.class, () -> jwtAuthenticationFilter.doFilter(mockRequest, mockResponse, mockFilterChain));
        assertEquals("Unable to validate token: decode token returned null", ex.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Bearer ", "bearer ", "BEARER "})
    void shouldReturnJwtForValidBearerToken(String tokenPrefix) {
        assertEquals(VALID_TOKEN, jwtAuthenticationFilter.extractJwtToken(tokenPrefix + VALID_TOKEN));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"ThisIsNotValid", "xxxx.yyyy.zzzz", "Bearer ThisIsNotValidEither", "Bearer aaaa.", "Bearer aaaaa.bbbbbbb.cccc.ddddddd", "Bearer .bbbb.cccc", "Bearer aaaaa..cccc", "Bearer aaaaa.bbbb.", "Bea", "Bearer "})
    void shouldThrowWhenTokenNotRightFormat(String token) {
        Exception ex = assertThrows(JwtException.class, () -> jwtAuthenticationFilter.extractJwtToken(token));
        assertEquals("Token is not a valid JWT", ex.getMessage());
    }

    @Test
    void shouldThrowWhenDecodeJwtReturnsNull() {
        when(jwtDecoder.decode(VALID_TOKEN)).thenReturn(null);
        Exception ex = assertThrows(JwtException.class, () -> jwtAuthenticationFilter.validateJwt(VALID_BEARER_TOKEN));

        assertEquals("Unable to validate token: decode token returned null", ex.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldThrowWhenUsernameIsInvalid(String username) {
        when(jwtDecoder.decode(VALID_TOKEN)).thenReturn(jwt(EXPECTED_CLIENT_ID,
                Instant.now(),
                Instant.now().plusSeconds(100),
                EXPECTED_SCOPES, username));
        Exception ex = assertThrows(JwtException.class, () -> jwtAuthenticationFilter.validateJwt(VALID_BEARER_TOKEN));

        assertEquals("Unable to validate token: token includes no valid username", ex.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "clientId2")
    @SneakyThrows
    void shouldNotAuthoriseWhenAudienceMismatch(String clientId) {
        when(appConfig.getEntraIdClientId()).thenReturn(clientId);
        when(jwtDecoder.decode(VALID_TOKEN)).thenReturn(testJwt);

        Exception ex = assertThrows(JwtException.class, () -> jwtAuthenticationFilter.validateJwt(VALID_BEARER_TOKEN));
        assertEquals("Unable to validate token: Audience mismatch", ex.getMessage());

    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "tenantId2")
    @SneakyThrows
    void shouldNotAuthoriseWhenTenantMismatch(String tenantId) {
        when(appConfig.getEntraIdClientId()).thenReturn(EXPECTED_CLIENT_ID);
        when(appConfig.getEntraIdTenantId()).thenReturn(tenantId);

        when(jwtDecoder.decode(VALID_TOKEN)).thenReturn(testJwt);
        Exception ex = assertThrows(JwtException.class, () -> jwtAuthenticationFilter.validateJwt(VALID_BEARER_TOKEN));
        assertEquals("Unable to validate token: Incorrect Tenant ID", ex.getMessage());

    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "appId2")
    @SneakyThrows
    void shouldNotAuthoriseWhenAppIdMismatch(String appId) {
        when(appConfig.getEntraIdClientId()).thenReturn(EXPECTED_CLIENT_ID);
        when(appConfig.getEntraIdTenantId()).thenReturn(EXPECTED_TENANT_ID);

        Jwt problemJwt = jwt(appId,
                Instant.now(),
                FUTURE_TIMESTAMP, EXPECTED_SCOPES, VALID_USER);
        when(jwtDecoder.decode("aaaa.bbbb.cccc")).thenReturn(problemJwt);
        Exception ex = assertThrows(JwtException.class, () -> jwtAuthenticationFilter.validateJwt(VALID_BEARER_TOKEN));
        assertTrue(ex.getMessage().contains("Unable to validate token"));
    }

    @Test
    void shouldThrowIfTokenWhenNotValidForCurrentTime() {
        when(appConfig.getEntraIdClientId()).thenReturn(EXPECTED_CLIENT_ID);
        when(appConfig.getEntraIdTenantId()).thenReturn(EXPECTED_TENANT_ID);

        when(jwtDecoder.decode(VALID_TOKEN)).thenReturn(jwt(EXPECTED_CLIENT_ID,
                FUTURE_TIMESTAMP, FUTURE_EXPIRY_TIMESTAMP,
                EXPECTED_SCOPES, VALID_USER));
        Exception ex = assertThrows(JwtException.class, () -> jwtAuthenticationFilter.validateJwt(VALID_BEARER_TOKEN));

        assertEquals("Unable to validate token: Token not valid for current time", ex.getMessage());
    }

    @Test
    void shouldThrowWhenValidBeforeThrows() {
        when(appConfig.getEntraIdClientId()).thenReturn(EXPECTED_CLIENT_ID);
        when(appConfig.getEntraIdTenantId()).thenReturn(EXPECTED_TENANT_ID);

        when(jwtDecoder.decode(VALID_TOKEN)).thenReturn(jwt(EXPECTED_CLIENT_ID,
                null, FUTURE_EXPIRY_TIMESTAMP,
                EXPECTED_SCOPES, VALID_USER));
        Exception ex = assertThrows(JwtException.class, () -> jwtAuthenticationFilter.validateJwt(VALID_BEARER_TOKEN));

        assertEquals("Unable to validate token: Token not before time is null", ex.getMessage());
    }

    @Test
    void shouldThrowWhenExpiryThrows() {
        when(appConfig.getEntraIdClientId()).thenReturn(EXPECTED_CLIENT_ID);
        when(appConfig.getEntraIdTenantId()).thenReturn(EXPECTED_TENANT_ID);

        when(jwtDecoder.decode(VALID_TOKEN)).thenReturn(jwt(EXPECTED_CLIENT_ID,
                PAST_TIMESTAMP, null,
                EXPECTED_SCOPES, VALID_USER));
        Exception ex = assertThrows(JwtException.class, () -> jwtAuthenticationFilter.validateJwt(VALID_BEARER_TOKEN));

        assertEquals("Unable to validate token: Token expiry is null", ex.getMessage());
    }

    @Test
    void shouldThrowWhenTokenIsExpired() {
        when(appConfig.getEntraIdClientId()).thenReturn(EXPECTED_CLIENT_ID);
        when(appConfig.getEntraIdTenantId()).thenReturn(EXPECTED_TENANT_ID);

        when(jwtDecoder.decode(VALID_TOKEN)).thenReturn(jwt(EXPECTED_CLIENT_ID,
                PAST_TIMESTAMP,
                PAST_EXPIRY_TIMESTAMP, EXPECTED_SCOPES, VALID_USER));
        Exception ex = assertThrows(JwtException.class, () -> jwtAuthenticationFilter.validateJwt(VALID_BEARER_TOKEN));

        assertEquals("Unable to validate token: Token is expired", ex.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "./default")
    @SneakyThrows
    void shouldNotAuthoriseWhenScopeMismatch(String scope) {
        when(appConfig.getEntraIdClientId()).thenReturn(EXPECTED_CLIENT_ID);
        when(appConfig.getEntraIdTenantId()).thenReturn(EXPECTED_TENANT_ID);

        List<String> scopes = new ArrayList<>();
        scopes.add(scope);

        Jwt problemJwt = jwt(EXPECTED_CLIENT_ID,
                Instant.now(),
                FUTURE_TIMESTAMP,
                scopes, VALID_USER);
        when(jwtDecoder.decode(VALID_TOKEN)).thenReturn(problemJwt);
        Exception ex = assertThrows(JwtException.class, () -> jwtAuthenticationFilter.validateJwt(VALID_BEARER_TOKEN));
        assertTrue(ex.getMessage().contains("Expected scope values are missing"));
    }

    @Test
    @SneakyThrows
    void shouldReturnFalseWhenBeforeNotBeforeTime() {
        Jwt decodedToken = jwt(EXPECTED_CLIENT_ID,
                FUTURE_TIMESTAMP, FUTURE_EXPIRY_TIMESTAMP,
                EXPECTED_SCOPES, VALID_USER);
        assertFalse(jwtAuthenticationFilter.isTokenValidForCurrentTime(decodedToken));
    }

    @Test
    @SneakyThrows
    void shouldThrowWhenNotBeforeTimeIsNull() {
        Jwt decodedToken = jwt(EXPECTED_CLIENT_ID,
                null,
                FUTURE_TIMESTAMP, EXPECTED_SCOPES, VALID_USER);
        Exception ex = assertThrows(JwtException.class, () -> jwtAuthenticationFilter.isTokenValidForCurrentTime(decodedToken));
        assertEquals("Token not before time is null", ex.getMessage());
    }

    @Test
    @SneakyThrows
    void shouldReturnTrueWhenAfterNotBeforeTime() {
        Jwt decodedToken = jwt(EXPECTED_CLIENT_ID,
                Instant.now(),
                FUTURE_TIMESTAMP, EXPECTED_SCOPES, VALID_USER);
        assertTrue(jwtAuthenticationFilter.isTokenValidForCurrentTime(decodedToken));
    }

    @Test
    @SneakyThrows
    void shouldReturnTrueWhenAfterExpiresTime() {
        Jwt decodedToken = jwt(EXPECTED_CLIENT_ID,
                PAST_TIMESTAMP, PAST_EXPIRY_TIMESTAMP,
                EXPECTED_SCOPES, VALID_USER);
        assertTrue(jwtAuthenticationFilter.isTokenExpired(decodedToken));
    }

    @Test
    @SneakyThrows
    void shouldThrowWhenExpiresTimeIsNull() {
        Jwt decodedToken = jwt(EXPECTED_CLIENT_ID,
                PAST_TIMESTAMP, null,
                EXPECTED_SCOPES, VALID_USER);
        Exception ex = assertThrows(JwtException.class, () -> jwtAuthenticationFilter.isTokenExpired(decodedToken));
        assertEquals("Token expiry is null", ex.getMessage());
    }

    @Test
    @SneakyThrows
    void shouldReturnFalseWhenBeforeExpiresTime() {
        Jwt decodedToken = jwt(EXPECTED_CLIENT_ID,
                Instant.now(),
                FUTURE_TIMESTAMP, EXPECTED_SCOPES, VALID_USER);
        assertFalse(jwtAuthenticationFilter.isTokenExpired(decodedToken));
    }

    @Test
    @SneakyThrows
    void shouldReturnTrueWhenScopeExists() {
        Jwt decodedToken = jwt(EXPECTED_CLIENT_ID,
                Instant.now(),
                FUTURE_TIMESTAMP, EXPECTED_SCOPES, VALID_USER);
        assertTrue(jwtAuthenticationFilter.hasValidScope(decodedToken, EXPECTED_SCOPES));
    }

    @Test
    @SneakyThrows
    void shouldReturnTrueWhenScopeDoesNotExists() {
        Jwt decodedToken = jwt(EXPECTED_CLIENT_ID,
                Instant.now(),
                FUTURE_TIMESTAMP, List.of("random scope"), VALID_USER);
        assertFalse(jwtAuthenticationFilter.hasValidScope(decodedToken, EXPECTED_SCOPES));
    }

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
}
