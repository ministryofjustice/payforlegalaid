package uk.gov.laa.gpfd.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import uk.gov.laa.gpfd.utils.RequestLogUtils;
import uk.gov.laa.gpfd.utils.SentryEvents;

import java.io.IOException;

@Slf4j
public class SentryAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    public SentryAuthenticationFailureHandler() {
        super("/login?error");
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        log.atWarn()
                .addKeyValue(RequestLogUtils.EVENT_ACTION, "authentication.failure")
                .addKeyValue(RequestLogUtils.EVENT_OUTCOME, "failure")
                .log("Authentication failed: {}", exception.getClass().getSimpleName());
        SentryEvents.captureException(exception, "authentication.failure");
        super.onAuthenticationFailure(request, response, exception);
    }
}