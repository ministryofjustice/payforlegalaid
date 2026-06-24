package uk.gov.laa.gpfd.config;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.laa.gpfd.utils.RequestLogUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class RequestResponseInterceptorTest {

    private ListAppender<ILoggingEvent> appender;

    @AfterEach
    void tearDown() {
        MDC.clear();
        if (appender != null) {
            Logger logger = (Logger) LoggerFactory.getLogger(RequestResponseInterceptor.class);
            logger.detachAppender(appender);
            appender.stop();
            appender = null;
        }
    }

    private ListAppender<ILoggingEvent> createListAppender() {
        Logger logger = (Logger) LoggerFactory.getLogger(RequestResponseInterceptor.class);
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
        List<org.slf4j.event.KeyValuePair> pairs = event.getKeyValuePairs();
        assertNotNull(pairs, "Expected structured key-value pairs but getKeyValuePairs() returned null");
        return pairs.stream()
                .collect(Collectors.toMap(kv -> kv.key, kv -> String.valueOf(kv.value)));
    }

    @Test
    void shouldSanitiseControlCharacters() {
        RequestResponseInterceptor interceptor = new RequestResponseInterceptor();
        String sanitized = interceptor.sanitise("/reports/\r\nFAKE");
        assertEquals("/reports/__FAKE", sanitized);
    }

    @Test
    void shouldNotThrowWhenUriContainsControlCharacters() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/reports/\r\nFORGED");

        RequestResponseInterceptor interceptor = new RequestResponseInterceptor();
        assertDoesNotThrow(() -> interceptor.preHandle(request, response, new Object()));
    }

    @Test
    void shouldLogSanitizedUri() {
        appender = createListAppender();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/test/\r\nFORGED");

        RequestResponseInterceptor interceptor = new RequestResponseInterceptor();
        interceptor.preHandle(request, response, new Object());

        String logMessage = appender.list.getFirst().getFormattedMessage();
        assertFalse(logMessage.contains("\r"));
        assertFalse(logMessage.contains("\n"));
    }

    @Test
    void shouldLogRequestWithCorrectStructure() {
        MDC.put(RequestLogUtils.REQUEST_ID, "test-request-id");
        MDC.put(RequestLogUtils.TRACE_ID, "test-trace-id");
        MDC.put(RequestLogUtils.USER_ID, "test-user-id");

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/reports");

        appender = createListAppender();
        RequestResponseInterceptor interceptor = new RequestResponseInterceptor();
        interceptor.preHandle(request, response, new Object());

        Map<String, String> keyValuePairs = extractKeyValuePairs(appender.list.get(0));

        assertEquals("http.request", keyValuePairs.get(RequestLogUtils.EVENT_ACTION));
        assertEquals("web", keyValuePairs.get("event.type"));
        assertEquals("GET", keyValuePairs.get("method"));
        assertEquals("/api/reports", keyValuePairs.get("uri"));
    }

    @Test
    void shouldLogResponseWithCorrectStructure() {
        MDC.put(RequestLogUtils.REQUEST_ID, "test-request-id");
        MDC.put(RequestLogUtils.TRACE_ID, "test-trace-id");
        MDC.put(RequestLogUtils.USER_ID, "test-user-id");

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/reports");
        when(response.getStatus()).thenReturn(200);

        appender = createListAppender();
        RequestResponseInterceptor interceptor = new RequestResponseInterceptor();
        interceptor.preHandle(request, response, new Object());
        interceptor.afterCompletion(request, response, new Object(), null);

        Map<String, String> keyValuePairs = extractKeyValuePairs(appender.list.get(1));

        assertEquals("http.response", keyValuePairs.get(RequestLogUtils.EVENT_ACTION));
        assertEquals("web", keyValuePairs.get("event.type"));
        assertEquals("success", keyValuePairs.get(RequestLogUtils.EVENT_OUTCOME));
        assertEquals("/api/reports", keyValuePairs.get("uri"));
        assertEquals("200", keyValuePairs.get("status"));
    }

    @Test
    void shouldLogWithoutDuplicateMdcKeys() {
        MDC.put(RequestLogUtils.REQUEST_ID, "test-request-id");
        MDC.put(RequestLogUtils.TRACE_ID, "test-trace-id");

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/reports");
        when(response.getStatus()).thenReturn(200);

        appender = createListAppender();
        RequestResponseInterceptor interceptor = new RequestResponseInterceptor();

        assertDoesNotThrow(() -> {
            interceptor.preHandle(request, response, new Object());
            interceptor.afterCompletion(request, response, new Object(), null);
        });
    }
}