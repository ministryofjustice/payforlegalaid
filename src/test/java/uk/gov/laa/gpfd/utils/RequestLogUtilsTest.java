package uk.gov.laa.gpfd.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RequestLogUtilsTest {

    @AfterEach
    void tearDown() {
        MDC.clear();
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldReturnRequestIdFromXRequestIdHeader() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getHeader("X-Request-Id")).thenReturn("req-123");

        String result = RequestLogUtils.getOrCreateRequestId(request);

        assertEquals("req-123", result);
    }

    @Test
    void shouldFallbackToCorrelationIdHeader() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getHeader("X-Request-Id")).thenReturn(null);
        when(request.getHeader("X-Correlation-Id")).thenReturn("corr-123");

        String result = RequestLogUtils.getOrCreateRequestId(request);

        assertEquals("corr-123", result);
    }

    @Test
    void shouldGenerateUuidWhenNoHeadersPresent() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getHeader(anyString())).thenReturn(null);

        String result = RequestLogUtils.getOrCreateRequestId(request);

        assertDoesNotThrow(() -> UUID.fromString(result));
    }

    @Test
    void shouldSanitizeRequestIdHeader() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getHeader("X-Request-Id"))
                .thenReturn("abc\r\n123");

        String result = RequestLogUtils.getOrCreateRequestId(request);

        assertEquals("abc__123", result);
    }

    @Test
    void shouldExtractAmazonTraceId() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getHeader("X-Amzn-Trace-Id"))
                .thenReturn("Root=1-67891233-abcdef012345678912345678;Parent=test");

        String result = RequestLogUtils.extractTraceId(request);

        assertEquals("1-67891233-abcdef012345678912345678", result);
    }

    @Test
    void shouldExtractTraceParentTraceId() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getHeader("X-Amzn-Trace-Id")).thenReturn(null);
        when(request.getHeader("traceparent"))
                .thenReturn("00-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-01");

        String result = RequestLogUtils.extractTraceId(request);

        assertEquals("4bf92f3577b34da6a3ce929d0e0e4736", result);
    }

    @Test
    void shouldExtractB3TraceId() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getHeader("X-Amzn-Trace-Id")).thenReturn(null);
        when(request.getHeader("traceparent")).thenReturn(null);
        when(request.getHeader("X-B3-TraceId")).thenReturn("b3-trace-id");

        String result = RequestLogUtils.extractTraceId(request);

        assertEquals("b3-trace-id", result);
    }

    @Test
    void shouldReturnNullWhenNoTraceHeadersPresent() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getHeader(anyString())).thenReturn(null);

        assertNull(RequestLogUtils.extractTraceId(request));
    }

    @Test
    void shouldPopulateMdcContext() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getHeader("X-Request-Id")).thenReturn("req-123");
        when(request.getHeader("X-Amzn-Trace-Id"))
                .thenReturn("Root=1-67891233-abcdef012345678912345678");

        RequestLogUtils.putRequestContext(request);

        assertEquals("req-123", MDC.get(RequestLogUtils.REQUEST_ID));
        assertEquals(
                "1-67891233-abcdef012345678912345678",
                MDC.get(RequestLogUtils.TRACE_ID)
        );
    }

    @Test
    void shouldClearContext() {
        MDC.put(RequestLogUtils.REQUEST_ID, "req");
        MDC.put(RequestLogUtils.TRACE_ID, "trace");
        MDC.put(RequestLogUtils.USER_ID, "user");

        RequestLogUtils.clearContext();

        assertNull(MDC.get(RequestLogUtils.REQUEST_ID));
        assertNull(MDC.get(RequestLogUtils.TRACE_ID));
        assertNull(MDC.get(RequestLogUtils.USER_ID));
    }

    @Test
    void shouldReturnNullWhenNoAuthenticationPresent() {
        assertNull(RequestLogUtils.extractUserIdFromSecurityContext());
    }

    @Test
    void shouldReturnNullWhenAuthenticationNotAuthenticated() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(false);

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(context);

        assertNull(RequestLogUtils.extractUserIdFromSecurityContext());
    }

    @Test
    void shouldExtractUserIdFromJwt() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject("user123")
                .build();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(jwt);

        String result = RequestLogUtils.extractUserId(authentication);

        assertEquals(
                UUID.nameUUIDFromBytes("user123".getBytes()).toString(),
                result
        );
    }

    @Test
    void shouldExtractUserIdFromStringPrincipal() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn("user123");

        String result = RequestLogUtils.extractUserId(authentication);

        assertEquals(
                UUID.nameUUIDFromBytes("user123".getBytes()).toString(),
                result
        );
    }

    @Test
    void shouldExtractUserIdFromOAuth2SubClaim() {
        OAuth2User user = new DefaultOAuth2User(
                Collections.emptyList(),
                Collections.singletonMap("sub", "user123"),
                "sub"
        );

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);

        String result = RequestLogUtils.extractUserId(authentication);

        assertEquals(
                UUID.nameUUIDFromBytes("user123".getBytes()).toString(),
                result
        );
    }

    @Test
    void shouldFallbackToAuthenticationNameForUnknownPrincipal() {
        Object principal = new Object();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(principal);
        when(authentication.getName()).thenReturn("fallbackUser");

        String result = RequestLogUtils.extractUserId(authentication);

        assertEquals(
                UUID.nameUUIDFromBytes("fallbackUser".getBytes()).toString(),
                result
        );
    }

    @Test
    void shouldExtractUserIdFromSecurityContext() {
        Authentication authentication = mock(Authentication.class);

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("user123");

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(context);

        String result = RequestLogUtils.extractUserIdFromSecurityContext();

        assertEquals(
                UUID.nameUUIDFromBytes("user123".getBytes()).toString(),
                result
        );
    }

    @Test
    void shouldReturnNullForNullAuthentication() {
        assertNull(RequestLogUtils.extractUserId(null));
    }

    @Test
    void shouldReturnNullForBlankPrincipalName() {
        Authentication authentication = mock(Authentication.class);

        when(authentication.getPrincipal()).thenReturn("");
        assertNull(RequestLogUtils.extractUserId(authentication));
    }

    @Test
    void shouldHandleMalformedTraceParent() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getHeader("X-Amzn-Trace-Id")).thenReturn(null);
        when(request.getHeader("traceparent")).thenReturn("invalid");

        assertEquals("invalid", RequestLogUtils.extractTraceId(request));
    }
}
