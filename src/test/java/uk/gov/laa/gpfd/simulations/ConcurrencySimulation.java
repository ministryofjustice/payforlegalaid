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
            .acceptHeader("application/json");

    FeederBuilder<String> feeder = csv("report-ids.csv").circular();

    ScenarioBuilder userJourney = scenario("Realistic User Journey")
            .feed(feeder)
            .exec(
                    http("List reports")
                            .get("/reports")
                            .check(status().is(200))
            )
            .pause(2)
            .exec(
                    http("View report detail [#{size}]")
                            .get("/reports/#{id}")
                            .check(status().is(200))
            )
            .pause(1)
            .exec(
                    http("Download [#{format}] [#{size}]")
                            .get("/reports/#{id}/#{format}")
                            .check(status().not(500))
                            .check(status().not(503))
            );

    {
        setUp(
                userJourney.injectOpen(
                        rampUsers(30).during(Duration.ofSeconds(30)),
                        constantUsersPerSec(30).during(Duration.ofSeconds(60)),
                        rampUsersPerSec(30).to(0).during(Duration.ofSeconds(15))
                )
        ).protocols(httpProtocol)
                .assertions(
                        global().failedRequests().count().is(0L)
                );
    }
}