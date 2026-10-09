package uk.gov.laa.gpfd.security;

import java.io.IOException;
import java.util.regex.Pattern;

import org.slf4j.spi.LoggingEventBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import uk.gov.laa.gpfd.utils.RequestLogUtils;

/**
 * Emits structured audit log events for Entra ID (OAuth2/OIDC) login outcomes.
 * <p>
 * Identifiers are logged in hashed form only (see {@link RequestLogUtils#extractUserId(Authentication)})
 * and no tokens, claims or IdP-supplied error descriptions are written to the logs.
 * </p>
 */
@Slf4j
public class OAuth2LoginAuditHandler implements AuthenticationSuccessHandler, AuthenticationFailureHandler {

    public static final String EVENT_ACTION = "authentication.oauth2.login";
    public static final String REGISTRATION_ID = "oauth2.client.registration_id";
    public static final String SOURCE_IP = "source.ip";
    public static final String ERROR_TYPE = "error.type";
    public static final String ERROR_CODE = "error.code";

    static final String SUCCESS_REDIRECT = "/";
    static final String FAILURE_REDIRECT = "/login?error";

    private static final String CALLBACK_PREFIX = "/login/oauth2/code/";
    private static final Pattern VALID_REGISTRATION_ID = Pattern.compile("[A-Za-z0-9_-]{1,64}");

    private final AuthenticationFailureHandler failureDelegate =
            new SimpleUrlAuthenticationFailureHandler(FAILURE_REDIRECT);

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        var event = auditEvent(log.atInfo(), request, "success");
        if (authentication instanceof OAuth2AuthenticationToken token) {
            event = event.addKeyValue(REGISTRATION_ID, token.getAuthorizedClientRegistrationId());
        }
        var userId = RequestLogUtils.extractUserId(authentication);
        if (userId != null) {
            event = event.addKeyValue(RequestLogUtils.USER_ID, userId);
        }
        event.log("Entra ID login succeeded");

        response.sendRedirect(SUCCESS_REDIRECT);
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        var event = auditEvent(log.atWarn(), request, "failure");
        var registrationId = registrationIdFromCallback(request);
        if (registrationId != null) {
            event = event.addKeyValue(REGISTRATION_ID, registrationId);
        }
        event = event.addKeyValue(ERROR_TYPE, exception.getClass().getSimpleName());
        if (exception instanceof OAuth2AuthenticationException oauth2Exception
                && oauth2Exception.getError() != null) {
            event = event.addKeyValue(ERROR_CODE, oauth2Exception.getError().getErrorCode());
        }
        event.log("Entra ID login failed");

        failureDelegate.onAuthenticationFailure(request, response, exception);
    }

    private static LoggingEventBuilder auditEvent(LoggingEventBuilder builder, HttpServletRequest request,
                                                  String outcome) {
        return builder
                .addKeyValue(RequestLogUtils.EVENT_ACTION, EVENT_ACTION)
                .addKeyValue(RequestLogUtils.EVENT_OUTCOME, outcome)
                .addKeyValue("event.type", "authentication")
                .addKeyValue("event.category", "authentication")
                .addKeyValue(SOURCE_IP, request.getRemoteAddr());
    }

    // Callback path is caller-controlled, so only log values that look like a registration id.
    private static String registrationIdFromCallback(HttpServletRequest request) {
        var uri = request.getRequestURI();
        if (uri == null || !uri.startsWith(CALLBACK_PREFIX)) {
            return null;
        }
        var candidate = uri.substring(CALLBACK_PREFIX.length());
        return VALID_REGISTRATION_ID.matcher(candidate).matches() ? candidate : null;
    }
}
