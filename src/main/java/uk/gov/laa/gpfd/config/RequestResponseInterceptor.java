package uk.gov.laa.gpfd.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.slf4j.MDC;
import org.slf4j.spi.LoggingEventBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import uk.gov.laa.gpfd.utils.RequestLogUtils;

@Slf4j
@Component
public class RequestResponseInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) {
        logRequest(request, handler);
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request,
                                @NonNull HttpServletResponse response,
                                @NonNull Object handler,
                                Exception ex) {
        logResponse(request, response, handler);

        if (ex != null) {
            log.atError()
                    .setCause(ex)
                    .addKeyValue(RequestLogUtils.EVENT_ACTION, "http.exception")
                    .addKeyValue(RequestLogUtils.EVENT_OUTCOME, "failure")
                    .addKeyValue(RequestLogUtils.REQUEST_ID, MDC.get(RequestLogUtils.REQUEST_ID))
                    .addKeyValue(RequestLogUtils.TRACE_ID, MDC.get(RequestLogUtils.TRACE_ID))
                    .addKeyValue(RequestLogUtils.USER_ID, RequestLogUtils.extractUserIdFromSecurityContext())
                    .log("Exception occurred during request processing");
        }
    }

    private void logRequest(HttpServletRequest request, Object handler) {
        LoggingEventBuilder logBuilder = log.atInfo()
                .addKeyValue(RequestLogUtils.EVENT_ACTION, "http.request")
                .addKeyValue("event.type", "web")
                .addKeyValue(RequestLogUtils.REQUEST_ID, MDC.get(RequestLogUtils.REQUEST_ID))
                .addKeyValue(RequestLogUtils.TRACE_ID, MDC.get(RequestLogUtils.TRACE_ID))
                .addKeyValue(RequestLogUtils.USER_ID, RequestLogUtils.extractUserIdFromSecurityContext())
                .addKeyValue("method", sanitise(request.getMethod()))
                .addKeyValue("uri", sanitise(request.getRequestURI()));

        addHandler(logBuilder, handler);

        logBuilder.log("Request received");
    }

    private void logResponse(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {
        LoggingEventBuilder logBuilder = log.atInfo()
                .addKeyValue(RequestLogUtils.EVENT_ACTION, "http.response")
                .addKeyValue("event.type", "web")
                .addKeyValue(RequestLogUtils.EVENT_OUTCOME, response.getStatus() >= 400 ? "failure" : "success")
                .addKeyValue(RequestLogUtils.REQUEST_ID, MDC.get(RequestLogUtils.REQUEST_ID))
                .addKeyValue(RequestLogUtils.TRACE_ID, MDC.get(RequestLogUtils.TRACE_ID))
                .addKeyValue(RequestLogUtils.USER_ID, RequestLogUtils.extractUserIdFromSecurityContext())
                .addKeyValue("uri", sanitise(request.getRequestURI()))
                .addKeyValue("status", response.getStatus());

        addHandler(logBuilder, handler);

        logBuilder.log("Completed request");
    }

    private void addHandler(LoggingEventBuilder logBuilder, Object handler) {
        if (handler instanceof HandlerMethod handlerMethod) {
            logBuilder.addKeyValue("handler", handlerMethod.getMethod().getName());
        }
    }

    String sanitise(String value) {
        if (value == null) {
            return null;
        }

        // Prevent CRLF / log forging attacks
        return value.replaceAll("\\p{Cntrl}", "_");
    }
}
