package uk.gov.laa.pfla.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import uk.gov.laa.pfla.scenario.ScenarioContext;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public record CommonSteps(ScenarioContext scenarioContext) {

    @Given("I am authenticated with a valid session")
    public void iAmAuthenticatedWithAValidSession() {
        scenarioContext.validAuthenticationCookieProvided();
    }

    @Given("I am authenticated with an invalid session")
    public void iAmAuthenticatedWithAnInvalidSession() {
        scenarioContext.invalidAuthenticationCookieProvided();
    }

    @Then("the service should respond with a status code of {int}")
    public void theServiceShouldRespondWithAStatusCodeOf(int expectedStatusCode) {
        var response = scenarioContext.getResponse();
        assertEquals(expectedStatusCode, response.getStatusCode().value(), "Expected status code " + expectedStatusCode);
    }

    @Then("the response should include the error message {string}")
    public void theResponseShouldIncludeTheErrorMessage(String errorMessage) {
        var response = scenarioContext.getResponseAs(String.class).getBody();

        assertAll("Verify response",
                () -> assertThat(response).isNotEmpty(),
                () -> assertThat(response).contains(errorMessage)
        );
    }

}
