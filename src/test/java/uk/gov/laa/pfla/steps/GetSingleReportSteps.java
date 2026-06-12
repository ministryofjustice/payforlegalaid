package uk.gov.laa.pfla.steps;

import com.azure.core.http.ContentType;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.http.HttpHeaders;
import uk.gov.laa.gpfd.model.GetReportById200Response;
import uk.gov.laa.gpfd.model.ReportsGet200Response;
import uk.gov.laa.gpfd.model.ReportsGet200ResponseReportListInner;
import uk.gov.laa.pfla.scenario.ScenarioContext;
import uk.gov.laa.pfla.service.HttpProvider;
import uk.gov.laa.pfla.util.DateParser;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public record GetSingleReportSteps(HttpProvider httpProvider, ScenarioContext scenarioContext) {

    private static final DateParser HTTP_DATE_PARSER = DateParser.rfc1123();

    @When("a request is made to the reports endpoint with a random report")
    public void aRequestIsMadeToTheReportsEndpointWithRandomReport() {
        scenarioContext.deserialize(ReportsGet200Response.class).getReportList().stream()
                .findAny()
                .map(ReportsGet200ResponseReportListInner::getId)
                .map(UUID::toString)
                .ifPresent(this::aRequestIsMadeToTheReportsEndpointWithTheReportId);
    }

    @When("a request is made to the reports endpoint with the report ID {string}")
    public void aRequestIsMadeToTheReportsEndpointWithTheReportId(String reportId) {
        var response = httpProvider.getClient().getForEntity("reports/" + reportId, String.class);
        scenarioContext.setResponse(response);
    }

    @Then("the response should include valid details for the resource")
    public void theResponseShouldIncludeValidDetailsForTheResource() {
        var response = scenarioContext.getResponseAs(String.class);

        assertThat(response.getHeaders())
                .describedAs("Response headers")
                .satisfies(headers -> {
                    assertThat(headers.get("Content-Type").get(0))
                            .describedAs("Content-Type should be JSON")
                            .isEqualToIgnoringCase(ContentType.APPLICATION_JSON);

                    assertThat(headers.get(HttpHeaders.DATE).get(0))
                            .describedAs("Should include valid Date header")
                            .isNotNull()
                            .satisfies(date -> assertThat(HTTP_DATE_PARSER.parse(date))
                                    .describedAs("Date header should be valid ISO-8601 format")
                                    .isBeforeOrEqualTo(Instant.now()));
                });

        assertThat(scenarioContext.deserialize(GetReportById200Response.class))
                .describedAs("Deserialized response body")
                .isNotNull()
                .satisfies(payload -> {
                    assertAll("Field validations",
                            () -> assertThat(payload.getId())
                                    .describedAs("Report ID")
                                    .isNotNull(),

                            () -> assertThat(payload.getReportDownloadUrl())
                                    .describedAs("Download URL")
                                    .isNotNull(),

                            () -> assertThat(payload.getReportName())
                                    .describedAs("Report name")
                                    .isNotNull()
                                    .isNotBlank()
                    );
                });
    }

}
