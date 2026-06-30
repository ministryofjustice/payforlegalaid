package uk.gov.laa.pfla.steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import uk.gov.laa.pfla.client.RestClient;
import uk.gov.laa.pfla.scenario.AuthenticationState;
import uk.gov.laa.pfla.scenario.ScenarioContext;
import uk.gov.laa.pfla.service.HttpProvider;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

public record GetS3StorageReportsSteps(HttpProvider httpProvider, ScenarioContext scenarioContext, RestClient restClient) {

    @When("a request is made to get the report details with the report ID {string}")
    public void aRequestIsMadeToTheReportDetailsWithTheReportId(String reportId) {

        AuthenticationState state = scenarioContext.getAuthenticationState();

        var response = httpProvider
                .getClient(restClient, state)
                .getForEntity(scenarioContext.url("/reports/" + reportId), String.class);

        scenarioContext.setResponse(response);
    }


    @Then("the response should include the file")
    public void verifyResponse() throws JsonProcessingException {
        var response = scenarioContext.getResponseAs(String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode json = objectMapper.readTree(response.getBody());

        assertAll("Verify JSON fields",
                () -> assertTrue(json.has("id"), "Response should contain id"),
                () -> assertTrue(json.has("reportName"), "Response should contain reportName"),
                () -> assertTrue(json.has("reportDownloadUrl"), "Response should contain reportDownloadUrl")
        );

    }

}