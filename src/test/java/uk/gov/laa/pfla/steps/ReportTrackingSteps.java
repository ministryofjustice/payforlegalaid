package uk.gov.laa.pfla.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import uk.gov.laa.pfla.configuration.SecurityConfigTestSetup;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public record ReportTrackingSteps(@Qualifier("trackingJdbcTemplate") JdbcTemplate trackingJdbcTemplate) {

    @Given("I have an empty tracking table")
    public void deleteTrackingEntries() {
        trackingJdbcTemplate.update("DELETE FROM glad.report_tracking WHERE user_id = ?", SecurityConfigTestSetup.getTestUserOid());
    }

    @Then("a row is entered in the report tracking table for report ID {string}")
    public void shouldBeAnEntryInTheReportTrackingTableForReport(String reportId) {

        var rowCount = trackingJdbcTemplate.queryForObject(
                "SELECT COUNT(id) FROM glad.report_tracking WHERE report_id = ? AND user_id = ?", Integer.class, UUID.fromString(reportId), SecurityConfigTestSetup.getTestUserOid()
        );

        assertThat(rowCount).isEqualTo(1);
    }

    @Then("no row is entered in the report tracking table for report ID {string}")
    public void shouldNotBeAnEntryInTheReportTrackingTableForReport(String reportId) {

        var rowCount = trackingJdbcTemplate.queryForObject(
                "SELECT COUNT(id) FROM glad.report_tracking WHERE report_id = ? AND user_id = ?", Integer.class, UUID.fromString(reportId), SecurityConfigTestSetup.getTestUserOid()
        );

        assertThat(rowCount).isEqualTo(0);
    }

}