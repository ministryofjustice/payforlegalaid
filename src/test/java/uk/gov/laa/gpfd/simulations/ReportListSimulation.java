package uk.gov.laa.gpfd.simulations;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class ReportListSimulation extends Simulation {
    String sessionCookie = System.getenv("JSESSIONID");

    HttpProtocolBuilder httpProtocol = http
            .baseUrl("https://dev-laa-get-payments-finance-data.cloud-platform.service.justice.gov.uk")
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
            .exec(session -> {
                String response = session.getString("responseBody");
                System.out.println("Response preview: " + response.substring(0, Math.min(200, response.length())));

                // Check if JSON contains reportList
                if (response.contains("reportList")) {
                    System.out.println("✅ JSON validation passed!");
                } else {
                    System.out.println("❌ JSON validation failed - no reportList found");
                }
                return session;
            })
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