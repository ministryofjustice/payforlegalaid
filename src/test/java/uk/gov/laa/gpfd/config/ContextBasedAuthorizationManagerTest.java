package uk.gov.laa.gpfd.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import uk.gov.laa.gpfd.utils.RequestLogUtils;

import java.util.UUID;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContextBasedAuthorizationManagerTest {

    @Mock
    private Supplier<Authentication> authenticationSupplier;

    @Mock
    private RequestAuthorizationContext context;

    @Mock
    private Authentication authentication;

    @AfterEach
    void tearDown() {
        MDC.clear();
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldThrowExceptionWhenContextIsNull() {
        ContextBasedAuthorizationManager manager = new ContextBasedAuthorizationManager();

        assertThrows(IllegalArgumentException.class, () -> 
            manager.authorize(authenticationSupplier, null)
        );
    }

    @Test
    void shouldReturnFalseWhenAuthenticationIsNull() {
        when(authenticationSupplier.get()).thenReturn(null);

        ContextBasedAuthorizationManager manager = new ContextBasedAuthorizationManager();
        AuthorizationResult result = manager.authorize(authenticationSupplier, context);

        assertFalse(result.isGranted());
    }

    @Test
    void shouldReturnFalseWhenAuthenticationNotAuthenticated() {
        when(authenticationSupplier.get()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        ContextBasedAuthorizationManager manager = new ContextBasedAuthorizationManager();
        AuthorizationResult result = manager.authorize(authenticationSupplier, context);

        assertFalse(result.isGranted());
    }

    @Test
    void shouldReturnGrantedWhenAuthenticated() {
        when(authenticationSupplier.get()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);

        ContextBasedAuthorizationManager manager = new ContextBasedAuthorizationManager();
        AuthorizationResult result = manager.authorize(authenticationSupplier, context);

        assertTrue(result.isGranted());
    }

    @Test
    void shouldLogAuthorizationDecisionWithMdcContext() {
        // Set up MDC context
        MDC.put(RequestLogUtils.REQUEST_ID, "test-request-id");
        MDC.put(RequestLogUtils.TRACE_ID, "test-trace-id");
        String userId = UUID.nameUUIDFromBytes("user123".getBytes()).toString();
        MDC.put(RequestLogUtils.USER_ID, userId);

        when(authenticationSupplier.get()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);

        ContextBasedAuthorizationManager manager = new ContextBasedAuthorizationManager();
        
        // This should log without throwing duplicate nested pairs error
        assertDoesNotThrow(() -> manager.authorize(authenticationSupplier, context));
    }

    @Test
    void shouldLogUnauthenticatedAttemptWithMdcContext() {
        // Set up MDC context
        MDC.put(RequestLogUtils.REQUEST_ID, "test-request-id");
        MDC.put(RequestLogUtils.TRACE_ID, "test-trace-id");

        when(authenticationSupplier.get()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        ContextBasedAuthorizationManager manager = new ContextBasedAuthorizationManager();
        
        // This should log without throwing duplicate nested pairs error
        assertDoesNotThrow(() -> manager.authorize(authenticationSupplier, context));
    }
}
