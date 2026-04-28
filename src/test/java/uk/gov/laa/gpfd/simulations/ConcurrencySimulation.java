package uk.gov.laa.gpfd.simulations;

import io.gatling.javaapi.core.FeederBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class ConcurrencySimulation extends Simulation {

    String sessionCookie = System.getenv("JSESSIONID");

    HttpProtocolBuilder httpProtocol = http
            .baseUrl(GatlingConfig.BASE_URL)
            .header("Cookie", "JSESSIONID=" + sessionCookie)
            .acceptHeader("application/json")
            .maxConnectionsPerHost(10);

    FeederBuilder<String> feeder = csv("report-ids.csv").circular();

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