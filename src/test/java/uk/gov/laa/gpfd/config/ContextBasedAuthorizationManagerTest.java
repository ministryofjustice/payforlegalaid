package uk.gov.laa.gpfd.config;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.slf4j.LoggerFactory;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import uk.gov.laa.gpfd.utils.RequestLogUtils;

import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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

    private ListAppender<ILoggingEvent> appender;

    @AfterEach
    void tearDown() {
        MDC.clear();
        SecurityContextHolder.clearContext();
        if (appender != null) {
            appender.stop();
        }
    }

    private ListAppender<ILoggingEvent> createListAppender() {
        Logger logger = (Logger) LoggerFactory.getLogger(ContextBasedAuthorizationManager.class);
        LoggerContext loggerContext = logger.getLoggerContext();

        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.setContext(loggerContext);
        listAppender.start();

        logger.addAppender(listAppender);
        logger.setLevel(Level.INFO);
        logger.setAdditive(false);

        return listAppender;
    }

    private Map<String, String> extractKeyValuePairs(ILoggingEvent event) {
        return event.getKeyValuePairs().stream()
                .collect(Collectors.toMap(kv -> kv.key, kv -> String.valueOf(kv.value)));
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

        appender = createListAppender();
        ContextBasedAuthorizationManager manager = new ContextBasedAuthorizationManager();
        AuthorizationResult result = manager.authorize(authenticationSupplier, context);

        assertNotNull(result);
        assertFalse(result.isGranted());

        assertFalse(appender.list.isEmpty(), "Expected a log event even when authentication is null");
        Map<String, String> keyValuePairs = extractKeyValuePairs(appender.list.getFirst());
        assertEquals("authorization.check", keyValuePairs.get(RequestLogUtils.EVENT_ACTION));
        assertEquals("failure", keyValuePairs.get(RequestLogUtils.EVENT_OUTCOME));
    }

    @Test
    void shouldReturnFalseWhenAuthenticationNotAuthenticated() {
        when(authenticationSupplier.get()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        ContextBasedAuthorizationManager manager = new ContextBasedAuthorizationManager();
        AuthorizationResult result = manager.authorize(authenticationSupplier, context);

        assertNotNull(result);
        assertFalse(result.isGranted());
    }

    @Test
    void shouldReturnGrantedWhenAuthenticated() {
        when(authenticationSupplier.get()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);

        ContextBasedAuthorizationManager manager = new ContextBasedAuthorizationManager();
        AuthorizationResult result = manager.authorize(authenticationSupplier, context);

        assertNotNull(result);
        assertTrue(result.isGranted());
    }

    @Test
    void shouldLogAuthorizationDecisionWithCorrectStructure() {
        MDC.put(RequestLogUtils.REQUEST_ID, "test-request-id");
        MDC.put(RequestLogUtils.TRACE_ID, "test-trace-id");
        String userId = UUID.nameUUIDFromBytes("user123".getBytes()).toString();
        MDC.put(RequestLogUtils.USER_ID, userId);

        when(authenticationSupplier.get()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);

        appender = createListAppender();
        ContextBasedAuthorizationManager manager = new ContextBasedAuthorizationManager();

        manager.authorize(authenticationSupplier, context);

        ILoggingEvent loggingEvent = appender.list.getFirst();
        Map<String, String> keyValuePairs = extractKeyValuePairs(loggingEvent);

        assertEquals("authorization.check", keyValuePairs.get(RequestLogUtils.EVENT_ACTION));
        assertEquals("success", keyValuePairs.get(RequestLogUtils.EVENT_OUTCOME));
        assertEquals("test-request-id", MDC.get(RequestLogUtils.REQUEST_ID));
        assertEquals("test-trace-id", MDC.get(RequestLogUtils.TRACE_ID));
        assertEquals(userId, MDC.get(RequestLogUtils.USER_ID));
    }

    @Test
    void shouldLogUnauthenticatedAttemptWithCorrectStructure() {
        MDC.put(RequestLogUtils.REQUEST_ID, "test-request-id");
        MDC.put(RequestLogUtils.TRACE_ID, "test-trace-id");

        when(authenticationSupplier.get()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        appender = createListAppender();
        ContextBasedAuthorizationManager manager = new ContextBasedAuthorizationManager();

        manager.authorize(authenticationSupplier, context);

        ILoggingEvent loggingEvent = appender.list.getFirst();
        Map<String, String> keyValuePairs = extractKeyValuePairs(loggingEvent);

        assertEquals("authorization.check", keyValuePairs.get(RequestLogUtils.EVENT_ACTION));
        assertEquals("failure", keyValuePairs.get(RequestLogUtils.EVENT_OUTCOME));
        assertEquals("test-request-id", MDC.get(RequestLogUtils.REQUEST_ID));
        assertEquals("test-trace-id", MDC.get(RequestLogUtils.TRACE_ID));
    }

    @Test
    void shouldProduceExactlyOneLogEventPerAuthorizationCall() {
        MDC.put(RequestLogUtils.REQUEST_ID, "test-request-id");
        MDC.put(RequestLogUtils.TRACE_ID, "test-trace-id");

        when(authenticationSupplier.get()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);

        appender = createListAppender();
        ContextBasedAuthorizationManager manager = new ContextBasedAuthorizationManager();

        manager.authorize(authenticationSupplier, context);

        assertEquals(1, appender.list.size(),
                "Expected exactly one log event per authorization call — duplicate MDC keys or repeated logging would produce more");
    }
}