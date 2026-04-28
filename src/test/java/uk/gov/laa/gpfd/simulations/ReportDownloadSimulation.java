package uk.gov.laa.gpfd.simulations;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.csv;
import static io.gatling.javaapi.core.CoreDsl.forAll;
import static io.gatling.javaapi.core.CoreDsl.global;
import static io.gatling.javaapi.core.CoreDsl.rampUsers;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import io.gatling.javaapi.core.FeederBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;
import io.gatling.javaapi.http.HttpProtocolBuilder;

public class ReportDownloadSimulation extends Simulation {

    String sessionCookie = System.getenv("JSESSIONID");

    HttpProtocolBuilder httpProtocol = http
            .baseUrl(GatlingConfig.BASE_URL)
            .header("Cookie", "JSESSIONID=" + sessionCookie);

    FeederBuilder<String> feeder = csv("report-ids.csv").circular();

    ScenarioBuilder downloadScenario = scenario("Report Download")
            .feed(feeder)
            .exec(
                    http("GET /reports/#{id}/#{format} [#{size}]")
                            .get("/reports/#{id}/#{format}")
                            .check(status().is(200))
            )
            .pause(2, 5);

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