package uk.gov.laa.pfla.hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import uk.gov.laa.gpfd.security.SilasRoles;
import uk.gov.laa.pfla.configuration.RoleRegistry;

import java.util.List;

public class RoleSetupHook {

    @Before
    public void beforeScenario(Scenario scenario) {
        List<String> roles = scenario.getSourceTagNames().stream()
                .filter(t -> t.startsWith("@Role="))
                .map(t -> resolveRole(t.replace("@Role=", "")))
                .toList();

        if (roles.isEmpty()) {
            roles = List.of(SilasRoles.FINANCIAL);
        }
        RoleRegistry.setRoles(roles);
    }

    @After
    public void afterScenario() {
        RoleRegistry.clear();
    }

    private static String resolveRole(String tagValue) {
        return switch (tagValue) {
            case "REP000" -> SilasRoles.REP000;
            case "Reconciliation" -> SilasRoles.RECONCILIATION;
            case "Financial" -> SilasRoles.FINANCIAL;
            default -> tagValue;
        };
    }
}
