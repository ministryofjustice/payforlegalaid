package uk.gov.laa.gpfd.config.builders;

import lombok.RequiredArgsConstructor;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.stereotype.Component;


/**
 * Custom builder for configuring session management in Spring Security, delegating the
 * session concurrency configuration to another customizer.
 * <p>
 * This class customizes the session management settings for HTTP security by utilizing
 * the {@link HttpSecuritySessionManagementConfigurerBuilder} to configure session
 * concurrency control. It allows for a more modular approach to session management
 * configuration.
 * </p>
 */
@Component
@RequiredArgsConstructor
public class SessionManagementConfigurerBuilder
        implements Customizer<SessionManagementConfigurer<HttpSecurity>> {

    /**
     * The custom {@link HttpSecuritySessionManagementConfigurerBuilder} responsible
     * for configuring session concurrency control, such as limiting the number of
     * concurrent sessions per user.
     * This dependency is injected through constructor injection due to the
     * {@link RequiredArgsConstructor} annotation.
     */
    private final HttpSecuritySessionManagementConfigurerBuilder concurrencyControlConfigurerCustomizer;

    /**
     * Customizes the session management settings for the provided {@link SessionManagementConfigurer}.
     * <p>
     * This method delegates the session concurrency control configuration to the
     * {@link HttpSecuritySessionManagementConfigurerBuilder} provided via the constructor.
     * The concurrency control settings (such as limiting the number of concurrent sessions)
     * are applied to the session management configuration.
     * </p>
     *
     * @param sessionManagement the {@link SessionManagementConfigurer<HttpSecurity>} object
     *                          used to configure session management for HTTP security.
     */
    @Override
    public void customize(SessionManagementConfigurer<HttpSecurity> sessionManagement) {
        sessionManagement.sessionConcurrency(concurrencyControlConfigurerCustomizer);
    }

}