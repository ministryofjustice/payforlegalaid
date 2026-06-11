package uk.gov.laa.gpfd.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.slf4j.MDC;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public final class RequestLogUtils {

    public static final String REQUEST_ID = "request_id";
    public static final String TRACE_ID = "trace.id";
    public static final String USER_ID = "user_id";
    public static final String EVENT_ACTION = "event.action";
    public static final String EVENT_OUTCOME = "event.outcome";

    private static final String X_REQUEST_ID_HEADER = "X-Request-Id";
    private static final String X_CORRELATION_ID_HEADER = "X-Correlation-Id";
    private static final String TRACEPARENT_HEADER = "traceparent";
    private static final String AWS_TRACE_ID_HEADER = "X-Amzn-Trace-Id";

    private RequestLogUtils() {
        // Utility class
    }

    public static String getOrCreateRequestId(HttpServletRequest request) {
        String requestId = getHeaderValue(request, X_REQUEST_ID_HEADER);
        if (requestId == null || requestId.isBlank()) {
            requestId = getHeaderValue(request, X_CORRELATION_ID_HEADER);
        }
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }
        return sanitizeHeaderValue(requestId);
    }

    public static String extractTraceId(HttpServletRequest request) {
        String amznTrace = getHeaderValue(request, AWS_TRACE_ID_HEADER);
        if (amznTrace != null && !amznTrace.isBlank()) {
            return parseAmazonTraceId(amznTrace);
        }

        String traceparent = getHeaderValue(request, TRACEPARENT_HEADER);
        if (traceparent != null && !traceparent.isBlank()) {
            return parseTraceParent(traceparent);
        }

        String b3TraceId = getHeaderValue(request, "X-B3-TraceId");
        if (b3TraceId != null && !b3TraceId.isBlank()) {
            return sanitizeHeaderValue(b3TraceId);
        }

        return null;
    }

    public static void putRequestContext(HttpServletRequest request) {
        MDC.put(REQUEST_ID, getOrCreateRequestId(request));
        String traceId = extractTraceId(request);
        if (traceId != null && !traceId.isBlank()) {
            MDC.put(TRACE_ID, traceId);
        }
    }

    public static void clearContext() {
        MDC.remove(REQUEST_ID);
        MDC.remove(TRACE_ID);
        MDC.remove(USER_ID);
    }

    public static String extractUserIdFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        return extractUserId(authentication);
    }

    public static String extractUserId(Authentication authentication) {
        if (authentication == null) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        String identifier = null;

        if (principal instanceof DefaultOidcUser oidcUser) {
            identifier = oidcUser.getSubject();
        } else if (principal instanceof OAuth2User oauth2User) {
            Object sub = oauth2User.getAttribute("sub");
            Object preferredUsername = oauth2User.getAttribute("preferred_username");
            identifier = sub != null ? sub.toString() : preferredUsername != null ? preferredUsername.toString() : authentication.getName();
        } else if (principal instanceof Jwt jwt) {
            identifier = jwt.getSubject();
        } else if (principal instanceof String principalString) {
            identifier = principalString;
        } else {
            identifier = authentication.getName();
        }

        if (identifier == null || identifier.isBlank()) {
            return null;
        }

        return hashIdentifier(identifier);
    }

    private static String hashIdentifier(String identifier) {
        return UUID.nameUUIDFromBytes(identifier.getBytes(StandardCharsets.UTF_8)).toString();
    }

    private static String getHeaderValue(HttpServletRequest request, String headerName) {
        if (request == null) {
            return null;
        }
        return request.getHeader(headerName);
    }

    private static String parseAmazonTraceId(String amznTrace) {
        String[] sections = amznTrace.split(";");
        for (String section : sections) {
            if (section.startsWith("Root=")) {
                return sanitizeHeaderValue(section.substring("Root=".length()));
            }
        }
        return sanitizeHeaderValue(amznTrace);
    }

    private static String parseTraceParent(String traceparent) {
        String[] parts = traceparent.split("-");
        if (parts.length >= 2) {
            return sanitizeHeaderValue(parts[1]);
        }
        return sanitizeHeaderValue(traceparent);
    }

    private static String sanitizeHeaderValue(String value) {
        if (value == null) {
            return null;
        }
        return value.replaceAll("\\p{Cntrl}", "_").trim();
    }
}
