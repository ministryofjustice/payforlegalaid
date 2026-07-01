package uk.gov.laa.pfla.steps;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.Cookie;
import com.microsoft.playwright.options.LoadState;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Value;
import uk.gov.laa.pfla.performance.PerformanceReportRegistry;
import uk.gov.laa.pfla.scenario.ScenarioContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PerformanceSteps {

    private final ScenarioContext scenarioContext;

    private Playwright playwright;
    private BrowserContext context;
    private Page page;

    @Value("${gpfd.url}")
    private String baseUrl;

    public PerformanceSteps(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    @Before("@performance")
    public void launchBrowser() {
        playwright = Playwright.create();
        scenarioContext.setBaseUrl(baseUrl);

        Browser browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
//                        .setHeadless(true)
        );

        context = browser.newContext(
                new Browser.NewContextOptions()
                        .setAcceptDownloads(true)
        );
        page = context.newPage();
// this to uat
        String sessionCookie = System.getenv("JSESSIONID");
        System.out.println("BASE URL IS " + baseUrl);
        if (sessionCookie != null && !sessionCookie.isEmpty()) {
            Cookie cookie = new Cookie("JSESSIONID", sessionCookie)
                    .setDomain(getDomainFromUrl(baseUrl))
                    .setPath("/")
                    .setSecure(baseUrl.startsWith("https"));
            context.addCookies(List.of(cookie));
        } else {
            System.out.println("WARNING: JSESSIONID env var not set. Set JSESSIONID=<cookie-value> to authenticate.");
        }
    }

    private String getDomainFromUrl(String url) {
        try {
            return java.net.URI.create(url).getHost();
        } catch (Exception _) {
            return "localhost";
        }
    }

    @After("@performance")
    public void closeBrowser() {
        if (playwright != null) {
            playwright.close();
        }
    }

    @When("the user navigates to the reports listing page")
    public void navigateToReportListPage() {
        page.navigate(scenarioContext.url("/reports"));
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    @Then("the page should load within {int} milliseconds")
    public void assertPageLoadTime(int thresholdMs) {
        Object result = page.evaluate("""
        () => {
            const nav = performance.getEntriesByType('navigation')[0];
            return nav.loadEventEnd - nav.startTime;
        }
    """);

        double loadTime = ((Number) result).doubleValue();

        System.out.printf("UI page load time: %.0fms (SLA: %dms)%n", loadTime, thresholdMs);
        assertThat(loadTime)
                .describedAs("Page load time should be under %dms", thresholdMs)
                .isLessThan(thresholdMs);
    }

    @When("the user downloads the {word} {word} report via the UI")
    public void downloadReport(String size, String format) {

        String reportId = PerformanceReportRegistry
                .getReportIdBySizeAndFormat(size, format)
                .orElseThrow(() -> new IllegalStateException(
                        "No report found for size: " + size + ", format: " + format
                ));

        String downloadUrl = scenarioContext.url("/reports/" + reportId + "/" + format);
        System.out.println("DEBUG: Downloading " + size + " " + format + " report, ID = " + reportId);
        System.out.println("DEBUG: URL = " + downloadUrl);

        long start = System.currentTimeMillis();
        scenarioContext.setAttribute("downloadStart", start);

        final long[] ttfb = {0};
        final int[] status = {0};
        
        page.onResponse(response -> {
            if (response.url().contains(reportId) && response.url().contains(format)) {
                status[0] = response.status();
                ttfb[0] = System.currentTimeMillis() - start;
                System.out.println("DEBUG: Response received: " + response.url() + " status=" + response.status() + " ttfb=" + ttfb[0] + "ms");
            }
        });

        try {
            // First navigate to reports page
            page.navigate(scenarioContext.url("/reports"));
            page.waitForLoadState(LoadState.DOMCONTENTLOADED);
            
            // Try to find and click the download link
            var downloadLink = page.locator("a[href*='/reports/" + reportId + "/" + format + "']");
            
            if (downloadLink.count() > 0) {
                // Click link and wait for download
                Download download = page.waitForDownload(
                        () -> downloadLink.first().click()
                );
                download.path(); // wait for completion
                
                long duration = System.currentTimeMillis() - start;
                scenarioContext.setAttribute("downloadDuration", duration);
                scenarioContext.setAttribute("ttfb", (double) ttfb[0]);
                System.out.println("DEBUG: Download completed in " + duration + "ms");
            } else {
                throw new IllegalStateException(
                        "Download link not found on page for " + size + " " + format + " report. " +
                        "Only testing active reports that are on the page."
                );
            }

        } catch (Exception e) {
            // Playwright throws "Download is starting" error when navigating to a download URL
            // This is expected behavior - we already captured TTFB from the response
            if (e.getMessage() != null && e.getMessage().contains("Download is starting")) {
                System.out.println("DEBUG: Download triggered (expected Playwright error), TTFB captured: " + ttfb[0] + "ms");
                long duration = System.currentTimeMillis() - start;
                scenarioContext.setAttribute("downloadDuration", duration);
                scenarioContext.setAttribute("ttfb", (double) ttfb[0]);
            } else {
                throw new IllegalStateException("Download failed: " + e.getMessage(), e);
            }
        }

        if (status[0] >= 400) {
            throw new IllegalStateException(
                    "Download request failed with status " + status[0] +
                            " for " + size + " " + format
            );
        }
    }

    @Then("the time to first byte should be within {int} milliseconds")
    public void assertTtfb(int thresholdMs) {

        double ttfb = (double) scenarioContext.getAttribute("ttfb");

        System.out.printf("TTFB: %.0fms (SLA: %dms)%n", ttfb, thresholdMs);

        assertThat(ttfb)
                .describedAs("TTFB should be under %dms", thresholdMs)
                .isLessThan(thresholdMs);
    }

    @Then("the download should complete within {int} milliseconds")
    public void assertDownloadCompletion(int thresholdMs) {

        long duration = (long) scenarioContext.getAttribute("downloadDuration");

        System.out.printf("Download duration: %dms (SLA: %dms)%n", duration, thresholdMs);

        assertThat(duration)
                .describedAs("Download should complete within %dms", thresholdMs)
                .isLessThan(thresholdMs);
    }

    private String getBaseUrl() {
        return baseUrl;
    }
}