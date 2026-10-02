package uk.gov.laa.gpfd.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.mock.env.MockEnvironment;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MockAuthAccessPolicyTest {

    @ParameterizedTest
    @ValueSource(strings = {"native", "framework"})
    void rejectsForwardedHeaderProcessing(String strategy) {
        var environment = new MockEnvironment().withProperty("server.forward-headers-strategy", strategy);
        assertThrows(IllegalStateException.class, () -> new MockAuthAccessPolicy(environment));
    }

    @ParameterizedTest
    @ValueSource(strings = {"uat", "prod"})
    void rejectsProtectedProfilesEvenWhenDisabled(String profile) {
        var environment = new MockEnvironment();
        environment.setProperty("gpfd.security.mock-auth.enabled", "false");
        environment.setActiveProfiles("mockauth", profile);
        assertThrows(IllegalStateException.class, () -> new MockAuthAccessPolicy(environment));
    }

    @ParameterizedTest
    @ValueSource(strings = {"uat", "prod"})
    void rejectsProtectedProfilesWhenEnabled(String profile) {
        var environment = new MockEnvironment();
        environment.setProperty("gpfd.security.mock-auth.enabled", "true");
        environment.setActiveProfiles("mockauth", profile);
        assertThrows(IllegalStateException.class, () -> new MockAuthAccessPolicy(environment));
    }

    @Test
    void requiresEveryOptIn() {
        var environment = new MockEnvironment();
        environment.setActiveProfiles("dev", "mockauth");
        assertFalse(new MockAuthAccessPolicy(environment).allows());
        environment.setProperty("gpfd.security.mock-auth.enabled", "true");
        assertFalse(new MockAuthAccessPolicy(environment).allows());
        environment.setProperty("gpfd.security.mock-auth.namespace", "laa-get-payments-finance-data-dev");
        assertTrue(new MockAuthAccessPolicy(environment).allows());
        environment.setProperty("gpfd.security.mock-auth.namespace", "other-namespace");
        assertFalse(new MockAuthAccessPolicy(environment).allows());
        environment.setProperty("gpfd.security.mock-auth.namespace", "laa-get-payments-finance-data-dev");
        environment.setActiveProfiles("mockauth");
        assertFalse(new MockAuthAccessPolicy(environment).allows());
    }
}