package uk.gov.laa.pfla.steps;

import com.azure.core.http.ContentType;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import uk.gov.laa.pfla.scenario.ScenarioContext;
import uk.gov.laa.pfla.service.HttpProvider;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public record GetCsvSteps(HttpProvider httpProvider, ScenarioContext scenarioContext) {

    @When("a request is made to the CSV endpoint with the report ID {string}")
    public void aRequestIsMadeToTheCsvEndpointWithTheReportId(String reportId) {
        var response = httpProvider.getClient().getForEntity(scenarioContext.url("/reports/" + reportId + "/csv"), String.class);
        scenarioContext.setResponse(response);
    }

    @Then("the response should include the CSV file")
    public void verifyCsvResponse() {
        var response = scenarioContext.getResponseAs(String.class);
        var headers = response.getHeaders();

        assertAll("Verify CSV response",
                () -> assertEquals(ContentType.APPLICATION_OCTET_STREAM, headers.getFirst("Content-Type"), "Content-Type should be Octet Stream"),
                () -> assertTrue(Objects.requireNonNull(headers.getFirst("Content-Disposition")).contains(".csv"), "Content-Disposition should include .csv"),
                () -> assertTrue(Objects.requireNonNull(headers.getFirst("Content-Disposition")).contains("attachment"), "Content-Disposition should indicate attachment"),
                () -> {
                    assertNotNull(response.getBody());
                    assertFalse(response.getBody().isEmpty(), "Response body should not be empty");
                }
        );
    }

}
