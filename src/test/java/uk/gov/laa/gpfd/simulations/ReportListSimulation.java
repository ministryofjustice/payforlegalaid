package uk.gov.laa.gpfd.simulations;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.csv;
import static io.gatling.javaapi.core.CoreDsl.jsonPath;
import static io.gatling.javaapi.core.CoreDsl.rampUsers;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import io.gatling.javaapi.core.FeederBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;
import io.gatling.javaapi.http.HttpProtocolBuilder;

public class ReportListSimulation extends Simulation {
    // Read session cookie from environment for authenticated report listing requests.
    String sessionCookie = System.getenv("JSESSIONID");

    // HTTP protocol settings for JSON requests and authenticated access.
    HttpProtocolBuilder httpProtocol = http
            .baseUrl(GatlingConfig.BASE_URL)
            .header("Cookie", "JSESSIONID=" + sessionCookie)
            .acceptHeader("application/json");

    // Cyclic feeder of report IDs for subsequent detail requests.
    FeederBuilder<String> feeder = csv("report-ids.csv").circular();

    // Scenario that first lists reports, then fetches details for one selected report.
    ScenarioBuilder scn = scenario("Report Listing")
            .exec(
                    http("GET /reports")
                            .get("/reports")
                            .check(status().is(200))
                            .check(jsonPath("$.reportList").exists())
            )
            .pause(1)
            .feed(feeder)
            .exec(
                    http("GET /reports/#{id}")
                            .get("/reports/#{id}")
                            .check(status().is(200))
            );

    // Inject a small load and keep the focus on list/detail correctness.
    {
        setUp(
                scn.injectOpen(
                        rampUsers(5).during(Duration.ofSeconds(30))
                )
        ).protocols(httpProtocol);
    }
}