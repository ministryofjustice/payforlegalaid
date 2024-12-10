package uk.gov.laa.gpfd.config.builders;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.stereotype.Component;


/**
 * Custom builder for configuring session management in Spring Security, specifically for
 * controlling concurrent sessions and handling session expiration redirection.
 * <p>
 * This class customizes the session management configuration by limiting the number
 * of concurrent sessions a user can have and specifying a URL to redirect to when
 * the session expires.
 * </p>
 */
@Component
public class HttpSecuritySessionManagementConfigurerBuilder
        implements Customizer<SessionManagementConfigurer<HttpSecurity>.ConcurrencyControlConfigurer> {

    private static final String EXPIRED_URL_REDIRECTION = "/login?expired";

    /**
     * Customizes the session concurrency control for HTTP security settings.
     * <p>
     * This method is called to configure session management, specifically to:
     * <ul>
     *   <li>Limit the maximum number of concurrent sessions to 1, ensuring that only one session is active per user.</li>
     *   <li>Set the URL to redirect to when a session expires to a login page with a query parameter indicating session expiration.</li>
     * </ul>
     * </p>
     *
     * @param sessionConcurrency the {@link SessionManagementConfigurer.HttpSecurity.ConcurrencyControlConfigurer} object
     *                           used to configure session concurrency settings.
     */
    @Override
    public void customize(SessionManagementConfigurer<HttpSecurity>.ConcurrencyControlConfigurer sessionConcurrency) {
        sessionConcurrency
                .maximumSessions(1)  // Limits the user to a single session
                .expiredUrl(EXPIRED_URL_REDIRECTION);  // Redirect to login page on session expiration
    }

}
