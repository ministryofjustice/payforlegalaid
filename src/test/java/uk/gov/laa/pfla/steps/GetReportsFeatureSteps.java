package uk.gov.laa.pfla.steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import uk.gov.laa.pfla.scenario.ScenarioContext;
import uk.gov.laa.pfla.service.HttpProvider;

import static org.junit.jupiter.api.Assertions.assertTrue;

public record GetReportsFeatureSteps(HttpProvider httpProvider, ScenarioContext scenarioContext) {

    @When("a request is made to the reports endpoint")
    public void aRequestIsMadeToTheReportsEndpoint() {
        var response = httpProvider.getClient().getForEntity("reports", String.class);
        scenarioContext.setResponse(response);
    }

    @Then("the response should include a list of all available reports")
    public void theResponseShouldIncludeAListOfAllAvailableReports() {
        var responseBody = scenarioContext.getResponseAs(String.class).getBody();
        assertTrue(responseBody != null && responseBody.contains("reportList"), "Expected response to include a list of reports");
    }

}
