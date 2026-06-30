package uk.gov.laa.pfla.steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import uk.gov.laa.pfla.scenario.ScenarioContext;
import uk.gov.laa.pfla.service.HttpProvider;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public record GetReportsFeatureSteps(HttpProvider httpProvider, ScenarioContext scenarioContext) {

    @When("a request is made to the reports endpoint")
    public void aRequestIsMadeToTheReportsEndpoint() {
        var response = httpProvider.getClient().getForEntity(scenarioContext.url("/reports"), String.class);
        scenarioContext.setResponse(response);
    }

    @Then("the response should include a list of all available reports")
    public void theResponseShouldIncludeAListOfAllAvailableReports() {
        var responseBody = scenarioContext.getResponseAs(String.class).getBody();
        assertTrue(responseBody != null && responseBody.contains("reportList"), "Expected response to include a list of reports");
    }

    @Then("the response should include a empty list")
    public void theResponseShouldIncludeEmptyReportList() {
        String responseBody = scenarioContext.getResponseAs(String.class).getBody();

        assertNotNull(responseBody, "Response body should not be null");
        assertTrue(responseBody.contains("\"reportList\":[]"), "Expected response to include an empty reportList");
    }

}
