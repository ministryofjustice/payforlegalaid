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
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
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

import static org.apache.commons.codec.digest.DigestUtils.sha256Hex;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, OutputCaptureExtension.class})
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
    private static final int TOKEN_ID_LENGTH = 8;

    @BeforeEach
    void beforeEach() {
        reset(mockRequest, mockRequest, mockFilterChain, jwtDecoder, appConfig);
        SecurityContextHolder.setContext(securityContext);
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtDecoder, appConfig);
    }

    @ParameterizedTest
    @SneakyThrows
    @NullAndEmptySource
    void shouldCallFilterWhenNoTokenProvided(String token, CapturedOutput output) {
        when(mockRequest.getHeader(JwtTokenComponents.HEADER_TYPE.value)).thenReturn(token);

        jwtAuthenticationFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain);

        verify(mockFilterChain).doFilter(mockRequest, mockResponse);
        verify(jwtDecoder, times(0)).decode(any());
        assertFalse(output.getOut().contains("token received, attempting validation"));
    }

    @SneakyThrows
    @Test
    void shouldCallFilterWhenValidTokenProvided(CapturedOutput output) {
        mockTokenExtractAndGetSysVars();
        when(jwtDecoder.decode(any())).thenReturn(jwt(EXPECTED_CLIENT_ID,
                Instant.now(),
                Instant.now().plusSeconds(100),
                EXPECTED_SCOPES, VALID_USER));

        jwtAuthenticationFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain);

        verify(mockFilterChain).doFilter(mockRequest, mockResponse);
        verify(jwtDecoder, times(1)).decode(any());
        assertTrue(output.getOut().contains("Token " + sha256Hex(VALID_BEARER_TOKEN).substring(0,TOKEN_ID_LENGTH) + " - token received, attempting validation"));
    }

    @Test
    void shouldThrowWhenDecodeJwtThrows() {
        when(mockRequest.getHeader(JwtTokenComponents.HEADER_TYPE.value)).thenReturn(VALID_BEARER_TOKEN);
        when(jwtDecoder.decode(any())).thenThrow(new IllegalArgumentException("decode has failed"));

        Exception ex = assertThrows(JwtException.class, () -> jwtAuthenticationFilter.doFilter(mockRequest, mockResponse, mockFilterChain));

        assertTrue(ex.getMessage().contains("Unable to validate token"));
        assertTrue(ex.getMessage().contains("IllegalArgumentException"));
        assertTrue(ex.getMessage().contains("decode has failed"));
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(strings = {"Bearer ", "bearer ", "BEARER "})
    void shouldReturnJwtForValidBearerToken(String tokenPrefix) {
        when(mockRequest.getHeader(JwtTokenComponents.HEADER_TYPE.value)).thenReturn(tokenPrefix + VALID_TOKEN);
        when(appConfig.getEntraIdClientId()).thenReturn(EXPECTED_CLIENT_ID);
        when(appConfig.getEntraIdTenantId()).thenReturn(EXPECTED_TENANT_ID);
        when(jwtDecoder.decode(VALID_TOKEN)).thenReturn(jwt(EXPECTED_CLIENT_ID,
                Instant.now(),
                Instant.now().plusSeconds(100),
                EXPECTED_SCOPES, VALID_USER));

        jwtAuthenticationFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain);

        verify(mockFilterChain).doFilter(mockRequest, mockResponse);
        verify(jwtDecoder, times(1)).decode(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"ThisIsNotValid", "xxxx.yyyy.zzzz", "Bearer ThisIsNotValidEither", "Bearer aaaa.", "Bearer aaaaa.bbbbbbb.cccc.ddddddd", "Bearer .bbbb.cccc", "Bearer aaaaa..cccc", "Bearer aaaaa.bbbb.", "Bea", "Bearer "})
    void shouldThrowWhenTokenNotRightFormat(String token) {
        when(mockRequest.getHeader(JwtTokenComponents.HEADER_TYPE.value)).thenReturn(token);

        Exception ex = assertThrows(JwtException.class, () -> jwtAuthenticationFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain));

        assertEquals("Token is not a valid JWT", ex.getMessage());
    }

    @Test
    void shouldThrowWhenValidateJwtThrows(CapturedOutput output) {
        when(mockRequest.getHeader(JwtTokenComponents.HEADER_TYPE.value)).thenReturn(VALID_BEARER_TOKEN);
        when(jwtDecoder.decode(any())).thenReturn(null);

        Exception ex = assertThrows(JwtException.class, () -> jwtAuthenticationFilter.doFilter(mockRequest, mockResponse, mockFilterChain));

        assertEquals("Unable to validate token: decode token returned null", ex.getMessage());
        assertFalse(output.getOut().contains("Token " + VALID_BEARER_TOKEN.hashCode() + " - JWT validated successfully"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldThrowWhenUsernameIsInvalid(String username) {
        when(mockRequest.getHeader(JwtTokenComponents.HEADER_TYPE.value)).thenReturn(VALID_BEARER_TOKEN);
        when(jwtDecoder.decode(any())).thenReturn(jwt(EXPECTED_CLIENT_ID,
                Instant.now(),
                Instant.now().plusSeconds(100),
                EXPECTED_SCOPES, username));

        Exception ex = assertThrows(JwtException.class, () -> jwtAuthenticationFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain));

        assertEquals("Unable to validate token: token includes no valid username", ex.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "clientId2")
    @SneakyThrows
    void shouldNotAuthoriseWhenAudienceMismatch(String clientId) {
        when(mockRequest.getHeader(JwtTokenComponents.HEADER_TYPE.value)).thenReturn(VALID_BEARER_TOKEN);
        when(appConfig.getEntraIdClientId()).thenReturn(clientId);
        when(jwtDecoder.decode(any())).thenReturn(testJwt);

        Exception ex = assertThrows(JwtException.class, () -> jwtAuthenticationFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain));

        assertEquals("Unable to validate token: Audience mismatch", ex.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "tenantId2")
    @SneakyThrows
    void shouldNotAuthoriseWhenTenantMismatch(String tenantId) {
        mockTokenExtractAndGetSysVars();
        when(appConfig.getEntraIdTenantId()).thenReturn("Another tenant Id");
        when(jwtDecoder.decode(any())).thenReturn(testJwt);

        Exception ex = assertThrows(JwtException.class, () -> jwtAuthenticationFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain));

        assertEquals("Unable to validate token: Incorrect Tenant ID", ex.getMessage());

    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "appId2")
    @SneakyThrows
    void shouldNotAuthoriseWhenAppIdMismatch(String appId) {
        Jwt problemJwt = jwt(appId,
                Instant.now(),
                FUTURE_TIMESTAMP, EXPECTED_SCOPES, VALID_USER);

        mockTokenExtractAndGetSysVars();
        when(jwtDecoder.decode(any())).thenReturn(problemJwt);

        Exception ex = assertThrows(JwtException.class, () -> jwtAuthenticationFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain));

        assertTrue(ex.getMessage().contains("Unable to validate token"));
    }

    @Test
    void shouldThrowIfTokenWhenNotValidForCurrentTime() {
        mockTokenExtractAndGetSysVars();
        when(jwtDecoder.decode(any())).thenReturn(jwt(EXPECTED_CLIENT_ID,
                FUTURE_TIMESTAMP, FUTURE_EXPIRY_TIMESTAMP,
                EXPECTED_SCOPES, VALID_USER));

        Exception ex = assertThrows(JwtException.class, () -> jwtAuthenticationFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain));

        assertEquals("Unable to validate token: Token not valid for current time", ex.getMessage());
    }

    @Test
    void shouldThrowWhenValidBeforeThrows() {
        mockTokenExtractAndGetSysVars();
        when(jwtDecoder.decode(any())).thenReturn(jwt(EXPECTED_CLIENT_ID,
                null, FUTURE_EXPIRY_TIMESTAMP,
                EXPECTED_SCOPES, VALID_USER));

        Exception ex = assertThrows(JwtException.class, () -> jwtAuthenticationFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain));

        assertEquals("Unable to validate token: Token not before time is null", ex.getMessage());
    }

    @Test
    void shouldThrowWhenExpiryThrows() {
        mockTokenExtractAndGetSysVars();
        when(jwtDecoder.decode(any())).thenReturn(jwt(EXPECTED_CLIENT_ID,
                PAST_TIMESTAMP, null,
                EXPECTED_SCOPES, VALID_USER));

        Exception ex = assertThrows(JwtException.class, () -> jwtAuthenticationFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain));

        assertEquals("Unable to validate token: Token expiry is null", ex.getMessage());
    }

    @Test
    void shouldThrowWhenTokenIsExpired() {
        mockTokenExtractAndGetSysVars();
        when(jwtDecoder.decode(any())).thenReturn(jwt(EXPECTED_CLIENT_ID,
                PAST_TIMESTAMP,
                PAST_EXPIRY_TIMESTAMP, EXPECTED_SCOPES, VALID_USER));

        Exception ex = assertThrows(JwtException.class, () -> jwtAuthenticationFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain));

        assertEquals("Unable to validate token: Token is expired", ex.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "./default")
    @SneakyThrows
    void shouldNotAuthoriseWhenScopeMismatch(String scope) {
        List<String> scopes = new ArrayList<>();
        scopes.add(scope);
        Jwt problemJwt = jwt(EXPECTED_CLIENT_ID,
                Instant.now(),
                FUTURE_TIMESTAMP,
                scopes, VALID_USER);

        mockTokenExtractAndGetSysVars();
        when(jwtDecoder.decode(any())).thenReturn(problemJwt);

        Exception ex = assertThrows(JwtException.class, () -> jwtAuthenticationFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain));

        assertTrue(ex.getMessage().contains("Expected scope values are missing"));
    }

    @Test
    @SneakyThrows
    void shouldReturnFalseWhenBeforeNotBeforeTime() {
        Jwt decodedToken = jwt(EXPECTED_CLIENT_ID,
                FUTURE_TIMESTAMP, FUTURE_EXPIRY_TIMESTAMP,
                EXPECTED_SCOPES, VALID_USER);

        mockTokenExtractAndGetSysVars();
        when(jwtDecoder.decode(any())).thenReturn(decodedToken);

        Exception ex = assertThrows(JwtException.class, () -> jwtAuthenticationFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain));
        assertEquals("Unable to validate token: Token not valid for current time", ex.getMessage());
    }

    @Test
    @SneakyThrows
    void shouldThrowWhenNotBeforeTimeIsNull() {
        Jwt decodedToken = jwt(EXPECTED_CLIENT_ID,
                null,
                FUTURE_TIMESTAMP, EXPECTED_SCOPES, VALID_USER);

        mockTokenExtractAndGetSysVars();
        when(jwtDecoder.decode(any())).thenReturn(decodedToken);

        Exception ex = assertThrows(JwtException.class, () -> jwtAuthenticationFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain));
        assertEquals("Unable to validate token: Token not before time is null", ex.getMessage());
    }

    @Test
    @SneakyThrows
    void shouldCallDoFilterWhenValidForCurrentTime() {
        Jwt decodedToken = jwt(EXPECTED_CLIENT_ID,
                Instant.now(),
                FUTURE_TIMESTAMP, EXPECTED_SCOPES, VALID_USER);

        mockTokenExtractAndGetSysVars();
        when(jwtDecoder.decode(any())).thenReturn(decodedToken);

        jwtAuthenticationFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain);

        verify(mockFilterChain).doFilter(mockRequest, mockResponse);
        verify(jwtDecoder, times(1)).decode(any());
    }

    @Test
    @SneakyThrows
    void shouldThrowWhenAfterExpiresTime() {
        Jwt decodedToken = jwt(EXPECTED_CLIENT_ID,
                PAST_TIMESTAMP, PAST_EXPIRY_TIMESTAMP,
                EXPECTED_SCOPES, VALID_USER);

        mockTokenExtractAndGetSysVars();
        when(jwtDecoder.decode(any())).thenReturn(decodedToken);

        Exception ex = assertThrows(JwtException.class, () -> jwtAuthenticationFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain));

        assertEquals("Unable to validate token: Token is expired", ex.getMessage());
    }

    @Test
    @SneakyThrows
    void shouldThrowWhenExpiresTimeIsNull() {
        Jwt decodedToken = jwt(EXPECTED_CLIENT_ID,
                PAST_TIMESTAMP, null,
                EXPECTED_SCOPES, VALID_USER);

        mockTokenExtractAndGetSysVars();
        when(jwtDecoder.decode(any())).thenReturn(decodedToken);

        Exception ex = assertThrows(JwtException.class, () -> jwtAuthenticationFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain));
        assertEquals("Unable to validate token: Token expiry is null", ex.getMessage());
    }

    private void mockTokenExtractAndGetSysVars() {
        when(mockRequest.getHeader(JwtTokenComponents.HEADER_TYPE.value)).thenReturn(VALID_BEARER_TOKEN);
        when(appConfig.getEntraIdClientId()).thenReturn(EXPECTED_CLIENT_ID);
        when(appConfig.getEntraIdTenantId()).thenReturn(EXPECTED_TENANT_ID);
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
