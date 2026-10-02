package uk.gov.laa.gpfd.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;

@Component
@Profile("mockauth")
public class MockAuthAccessPolicy {

    private final boolean enabled;

    public MockAuthAccessPolicy(Environment environment) {
        if (environment.acceptsProfiles(Profiles.of("uat", "prod"))) {
            throw new IllegalStateException("mockauth must not be active with uat or prod");
        }
        if (!"none".equalsIgnoreCase(environment.getProperty("server.forward-headers-strategy", "none"))) {
            throw new IllegalStateException("mockauth requires server.forward-headers-strategy=none");
        }
        enabled = environment.acceptsProfiles(Profiles.of("dev"))
                && environment.getProperty("gpfd.security.mock-auth.enabled", Boolean.class, false)
                && "laa-get-payments-finance-data-dev".equals(
                        environment.getProperty("gpfd.security.mock-auth.namespace"));
    }

    public boolean allows() {
        return enabled;
    }
}