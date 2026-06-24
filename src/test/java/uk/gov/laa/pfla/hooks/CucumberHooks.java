package uk.gov.laa.pfla.hooks;

import io.cucumber.java.Before;
import org.springframework.boot.test.web.server.LocalServerPort;
import uk.gov.laa.pfla.scenario.ScenarioContext;

public class CucumberHooks {

    private final ScenarioContext scenarioContext;
    private final int port;

    public CucumberHooks(
            ScenarioContext scenarioContext,
            @LocalServerPort int port
    ) {
        this.scenarioContext = scenarioContext;
        this.port = port;
    }

    @Before
    public void setupBaseUrl() {
        scenarioContext.setBaseUrl("http://localhost:" + port);
    }
}
