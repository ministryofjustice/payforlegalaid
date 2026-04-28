package uk.gov.laa.gpfd.simulations;

import io.gatling.javaapi.core.FeederBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class ReportDownloadSimulation extends Simulation {

    String sessionCookie = System.getenv("JSESSIONID");

    HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:8080")
            .header("Cookie", "JSESSIONID=" + sessionCookie);

    FeederBuilder<String> feeder = csv("report-ids.csv").circular();

    ScenarioBuilder downloadScenario = scenario("Report Download")
            .feed(feeder)
            .exec(
                    http("GET /reports/#{id}/#{format} [#{size}]")
                            .get("/reports/#{id}/#{format}")
                            .check(status().is(200))
                            .check(headerRegex("Content-Disposition", "attachment").exists())
            );

    {
        setUp(
                downloadScenario.injectOpen(
                        rampUsers(20).during(Duration.ofSeconds(60))
                )
        ).protocols(httpProtocol)
                .assertions(
                        forAll().responseTime().percentile(95).lt(10000),
                        global().failedRequests().percent().lt(0.1)
                );
    }
}