package uk.gov.laa.gpfd.config;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import ch.qos.logback.classic.Logger;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ActiveProfiles("testauth")
public class RequestResponseInterceptorTest {
    @Test
    void shouldSanitiseControlCharacters() {

        RequestResponseInterceptor interceptor =
                new RequestResponseInterceptor();

        String sanitized =
                interceptor.sanitise("/reports/\r\nFAKE");

        assertEquals("/reports/__FAKE", sanitized);
    }

    @Test
    void shouldNotThrowWhenUriContainsControlCharacters() {

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI())
                .thenReturn("/reports/\r\nFORGED");

        RequestResponseInterceptor interceptor =
                new RequestResponseInterceptor();

        assertDoesNotThrow(() ->
                interceptor.preHandle(request, response, new Object()));
    }

    @Test
    void shouldLogSanitizedUri() {

        Logger logger =
                (Logger) LoggerFactory.getLogger(RequestResponseInterceptor.class);

        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();

        logger.addAppender(appender);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI())
                .thenReturn("/test/\r\nFORGED");

        RequestResponseInterceptor interceptor =
                new RequestResponseInterceptor();

        interceptor.preHandle(request, response, new Object());

        String logMessage =
                appender.list.getFirst().getFormattedMessage();

        assertFalse(logMessage.contains("\r"));
        assertFalse(logMessage.contains("\n"));
    }
}
