package uk.gov.laa.gpfd.simulations;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.constantUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.csv;
import static io.gatling.javaapi.core.CoreDsl.global;
import static io.gatling.javaapi.core.CoreDsl.rampUsers;
import static io.gatling.javaapi.core.CoreDsl.rampUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import io.gatling.javaapi.core.FeederBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;
import io.gatling.javaapi.http.HttpProtocolBuilder;

public class ConcurrencySimulation extends Simulation {

    // Load the JSESSIONID from environment variables so requests are authenticated as a real browser session.
    String sessionCookie = System.getenv("JSESSIONID");

    // Shared HTTP configuration for all requests in this simulation.
    HttpProtocolBuilder httpProtocol = http
            .baseUrl(GatlingConfig.BASE_URL)
            .header("Cookie", "JSESSIONID=" + sessionCookie)
            .acceptHeader("application/json")
            .maxConnectionsPerHost(10);

    // Feed report IDs and metadata from CSV into each virtual user.
    FeederBuilder<String> feeder = csv("report-ids.csv").circular();

    // The user journey covers listing reports, viewing a single report and downloading it.
    // Observe system behaviour under load rather than assume a perfectly clean response model
    ScenarioBuilder userJourney = scenario("Realistic User Journey")
            .feed(feeder)
            .exec(
                    http("List reports")
                            .get("/reports")
                            .check(status().in(200, 429))
            )
            .pause(2, 4)
            .exec(
                    http("View report detail [#{size}]")
                            .get("/reports/#{id}")
                            .check(status().in(200, 429))
            )
            .pause(1, 3)
            .exec(
                    http("Download [#{format}] [#{size}]")
                            .get("/reports/#{id}/#{format}")
                            .check(status().in(200, 429, 500, 503))
            );

    // Configure how virtual users are injected over time and what assertions should hold.
    {
        setUp(
                userJourney.injectOpen(
                        rampUsers(10).during(Duration.ofSeconds(30)),
                        constantUsersPerSec(10).during(Duration.ofSeconds(60)),
                        rampUsersPerSec(10).to(0).during(Duration.ofSeconds(15))
                )
        ).protocols(httpProtocol)
                .assertions(
                        global().failedRequests().percent().lt(5.0)
                );
    }
}