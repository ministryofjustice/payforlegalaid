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
    String sessionCookie = System.getenv("JSESSIONID");

    HttpProtocolBuilder httpProtocol = http
            .baseUrl(GatlingConfig.BASE_URL)
            .header("Cookie", "JSESSIONID=" + sessionCookie)
            .acceptHeader("application/json");

    FeederBuilder<String> feeder = csv("report-ids.csv").circular();

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

    {
        setUp(
                scn.injectOpen(
                        rampUsers(5).during(Duration.ofSeconds(30))
                )
        ).protocols(httpProtocol);
    }
}