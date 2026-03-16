package uk.gov.laa.gpfd.config.builders;

import lombok.SneakyThrows;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

/**
 * Custom builder for configuring HTTP request authorization in Spring Security.
 * <p>
 * This class customizes the authorization settings for various HTTP request patterns,
 * allowing specific endpoints to be publicly accessible and securing others with authentication.
 * Spring Security 7 automatically implements any Customizer Beans so do not make this a Bean or there will be errors
 * </p>
 */
public class AuthorizeHttpRequestsBuilder
        implements Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> {

    /**
     * Endpoint pattern for the Actuator endpoints, which expose management and health status information.
     */
    private static final String ACTUATOR_ENDPOINT = "/actuator/**";

    /**
     * Endpoint pattern for the health check of the application.
     * This is part of the Actuator module and is often used to monitor the application status.
     */
    private static final String HEALTH_ENDPOINT = "/actuator/health";

    /**
     * Endpoint pattern for Swagger UI, which is used to interact with API documentation.
     */
    private static final String SWAGGER_UI = "/swagger-ui/**";

    /**
     * Endpoint for accessing the Swagger YAML file containing API documentation.
     */
    private static final String SWAGGER_FILE = "/swagger.yml";

    /**
     * Endpoint pattern for the OpenAPI documentation (v3), which provides details about the REST API.
     */
    private static final String API_DOCS_ROOT = "/v3/**";

    private final AuthorizationManager<RequestAuthorizationContext> authManager;

    public AuthorizeHttpRequestsBuilder(AuthorizationManager<RequestAuthorizationContext> authManager) {
        this.authManager = authManager;
    }

    /**
     * Customizes HTTP request authorization settings for the application.
     * <p>
     * This method configures the authorization rules for various URL patterns:
     * <ul>
     *   <li>{@link #API_DOCS_ROOT}, {@link #SWAGGER_UI}, and {@link #SWAGGER_FILE} are configured to be publicly accessible (no authentication required).</li>
     *   <li>{@link #ACTUATOR_ENDPOINT} and {@link #HEALTH_ENDPOINT} are also publicly accessible, allowing external services or monitoring systems to check the application status.</li>
     *   <li>All other requests require authentication, ensuring that only authenticated users can access protected resources.</li>
     * </ul>
     * </p>
     *
     * @param authorizationManagerRequestMatcherRegistry the {@link AuthorizeHttpRequestsConfigurer.AuthorizationManagerRequestMatcherRegistry} object
     *                                                   used to configure the authorization rules for various HTTP request patterns.
     */
    @SneakyThrows
    @Override
    public void customize(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authorizationManagerRequestMatcherRegistry) {
        authorizationManagerRequestMatcherRegistry
                .requestMatchers(API_DOCS_ROOT, SWAGGER_UI, SWAGGER_FILE).permitAll()  // Allow unrestricted access to API docs and Swagger UI
                .requestMatchers(ACTUATOR_ENDPOINT, HEALTH_ENDPOINT).permitAll()         // Allow unrestricted access to actuator and health endpoints
                .requestMatchers("/login").permitAll()
                .anyRequest().access(authManager);  // Require authentication for all other requests
    }

}
