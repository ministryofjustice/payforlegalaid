package uk.gov.laa.gpfd.config;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.SystemEnvironmentPropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.env.MockEnvironment;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SessionTimeoutConfigurationTest {

    @ParameterizedTest
    @ValueSource(strings = {"dev", "uat", "prod"})
    void shouldConfigureFifteenMinuteSessionTimeout(String profile)
            throws IOException {
        var environment = loadProfileProperties(profile);

        assertEquals(Duration.ofMinutes(15), sessionTimeout(environment), "Session timeout for profile: " + profile);
    }

    @ParameterizedTest
    @ValueSource(strings = {"dev", "uat", "prod"})
    void shouldAllowEnvironmentVariableToOverrideSessionTimeout(String profile)
            throws IOException {
        var environment = loadProfileProperties(profile);
        environment.getPropertySources().addFirst(
                new SystemEnvironmentPropertySource(
                        "systemEnvironment",
                        Map.<String, Object>of(
                                "SERVER_SERVLET_SESSION_TIMEOUT", "1m")));

        assertEquals(Duration.ofMinutes(1), sessionTimeout(environment), "Session timeout for profile: " + profile);
    }

    private MockEnvironment loadProfileProperties(String profile) throws IOException {
        var environment = new MockEnvironment();
        var resource = new ClassPathResource("application-" + profile + ".yml");

        for (var source : new YamlPropertySourceLoader().load(profile, resource)) {
            environment.getPropertySources().addLast(source);
        }

        return environment;
    }

    private Duration sessionTimeout(MockEnvironment environment) {
        return Binder.get(environment)
                .bind("server.servlet.session.timeout", Duration.class)
                .orElseThrow(() -> new AssertionError( "Missing server.servlet.session.timeout"));
    }
}