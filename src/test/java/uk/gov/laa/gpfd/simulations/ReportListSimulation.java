package uk.gov.laa.gpfd.simulations;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class ReportListSimulation extends Simulation {
    String sessionCookie = System.getenv("JSESSIONID");

    HttpProtocolBuilder httpProtocol = http
            .baseUrl(GatlingConfig.BASE_URL)
            .header("Cookie", "JSESSIONID=" + sessionCookie)
            .acceptHeader("application/json");

    ScenarioBuilder scn = scenario("Report Listing")
            .exec(
                    http("GET /reports")
                            .get("/reports")
                            .check(status().is(200))
                            .check(jsonPath("$.reportList").exists())
                            .check(bodyString().saveAs("responseBody"))
            )
            .exec(session -> session)
            .pause(1)
            .exec(
                    http("GET /reports/{id}")
                            .get("/reports/f46b4d3d-c100-429a-bf9a-6c3305dbdbfa")
                            .check(status().is(200))
            );

    {
        setUp(
                scn.injectOpen(
                        rampUsers(5).during(Duration.ofSeconds(30))  // Reduce to 5 users for debugging
                )
        ).protocols(httpProtocol);
    }
}