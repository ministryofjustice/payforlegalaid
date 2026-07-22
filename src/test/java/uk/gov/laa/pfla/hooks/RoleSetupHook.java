package uk.gov.laa.pfla.hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import uk.gov.laa.pfla.configuration.RoleRegistry;

import java.util.List;

public class RoleSetupHook {

    @Before
    public void beforeScenario(Scenario scenario) {
        List<String> roles = scenario.getSourceTagNames().stream()
                .filter(t -> t.startsWith("@Role="))
                .map(t -> t.replace("@Role=", ""))
                .toList();

        //set Default role = Financial
        if (roles.isEmpty()) {
            roles = List.of("Financial");
        }
        RoleRegistry.setRoles(roles);
    }

    @After
    public void afterScenario() {
        RoleRegistry.clear();
    }
}