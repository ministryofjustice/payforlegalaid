package uk.gov.laa.gpfd.config.builders;

import lombok.SneakyThrows;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

import static com.azure.spring.cloud.autoconfigure.implementation.aad.security.AadWebApplicationHttpSecurityConfigurer.aadWebApplication;

/**
 * Custom builder for configuring HTTP request authorization in Spring Security.
 * <p>
 * This class customizes the authorization settings for various HTTP request patterns,
 * allowing specific endpoints to be publicly accessible and securing others with authentication.
 * </p>
 */
@Component
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

    /**
     * Endpoint for the login page, which allows users to initiate authentication.
     */
    private static final String LOGIN_ENDPOINT = "/login";

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
     * @param authorizationManagerRequestMatcherRegistry the {@link AuthorizeHttpRequestsConfigurer.HttpSecurity.AuthorizationManagerRequestMatcherRegistry} object
     *                                                   used to configure the authorization rules for various HTTP request patterns.
     */
    @SneakyThrows
    @Override
    public void customize(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authorizationManagerRequestMatcherRegistry) {
        authorizationManagerRequestMatcherRegistry.and()
                .apply(aadWebApplication()).and()
                .authorizeHttpRequests()
                .requestMatchers(API_DOCS_ROOT, SWAGGER_UI, SWAGGER_FILE).permitAll()  // Allow unrestricted access to API docs and Swagger UI
                .requestMatchers(ACTUATOR_ENDPOINT, HEALTH_ENDPOINT).permitAll()      // Allow unrestricted access to actuator and health endpoints
                .requestMatchers(LOGIN_ENDPOINT).permitAll()                          // Allow unrestricted access to login endpoint
                .anyRequest().authenticated();                                        // Require authentication for all other requests
    }

}
