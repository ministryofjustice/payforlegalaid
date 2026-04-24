package uk.gov.laa.pfla.steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import uk.gov.laa.pfla.scenario.ScenarioContext;
import uk.gov.laa.pfla.service.HttpProvider;

import static org.junit.jupiter.api.Assertions.assertTrue;

public record HealthSteps(HttpProvider httpProvider, ScenarioContext scenarioContext) {

    @When("a request is made to the actuator health endpoint")
    public void callActuatorApi() {
        var response = httpProvider.getClient().getForEntity("actuator/health", String.class);
        scenarioContext.setResponse(response);
    }

    @Then("the response should indicate a healthy state")
    public void theResponseShouldIndicateHealthyState() {
        var responseBody = scenarioContext.getResponseAs(String.class).getBody();
        assertTrue(responseBody != null && responseBody.contains("\"status\":\"UP\""), "Expected response to indicate a healthy state");
    }

}
