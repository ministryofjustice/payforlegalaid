package uk.gov.laa.gpfd.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class RequestResponseInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.info("Request received: {} {} ({})", request.getMethod(), request.getRequestURI(), ((HandlerMethod) handler).getMethod().getName());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        log.info("Completed request: {} with status {} ({})", request.getRequestURI(), response.getStatus(), ((HandlerMethod) handler).getMethod().getName());
        if (ex != null) {
            log.error("Exception occurred: {}", ex.getMessage());
        }
    }
}
