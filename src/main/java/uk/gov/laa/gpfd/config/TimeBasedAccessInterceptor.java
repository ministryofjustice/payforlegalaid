package uk.gov.laa.gpfd.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import uk.gov.laa.gpfd.exception.ServiceUnavailableException;

import java.time.LocalTime;

/**
 * Class intercepts api call, allowing endpoint response to reflect  out of hours service availability
 */
@Component
public class TimeBasedAccessInterceptor implements HandlerInterceptor {

    private final TimeProvider timeProvider;
    private final LocalTime startTime = LocalTime.of(7, 0);  // 7 AM
    private final LocalTime endTime = LocalTime.of(22, 0);   // 10 PM

    public TimeBasedAccessInterceptor(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServiceUnavailableException {
        LocalTime now = timeProvider.getCurrentTime();
        if (now.isBefore(startTime) || now.isAfter(endTime)) {
            throw new ServiceUnavailableException("The service is unavailable between the hours of 22:00 and 07:00, Mon - Sun");
        }
        return true;
    }
}