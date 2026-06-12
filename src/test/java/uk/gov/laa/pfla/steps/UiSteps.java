package uk.gov.laa.pfla.steps;

import com.deque.html.axecore.playwright.AxeBuilder;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Response;
import com.microsoft.playwright.options.AriaRole;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.laa.pfla.client.AuthenticationProvider;
import uk.gov.laa.pfla.configuration.PlaywrightManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@Slf4j
public class UiSteps {

    private Page page;
    private int reportCount;
    private Response response;

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Before("@ui")
    public void setupUI(){
        System.out.println("Ensuring Playwright is setup");
        PlaywrightManager.getPlaywright();
    }

    @After("@ui")
    public void cleanupPage(){
        System.out.println("Cleaning up page from last test");
        page.close();
        response = null;
    }

    @Given("The user is authorised in the UI")
    public void uiAuthSetup(){
        System.out.println("Creating an authorised test page");
        Browser browser = PlaywrightManager.getBrowser();
        BrowserContext browserContext = browser.newContext(new Browser.NewContextOptions()
                .setExtraHTTPHeaders(authenticationProvider.setAuthHeader().toSingleValueMap()));
        page = browserContext.newPage();
    }

    @Given("The user is not authorised in the UI")
    public void uiNoAuthSetup(){
        System.out.println("Creating an unauthorised test page");
        Browser browser = PlaywrightManager.getBrowser();
        page = browser.newPage();
    }

    @When("I load the GLAD page")
    public void loadGladPage() {
        response = page.navigate("http://localhost:8080/");
    }

    @Then("the page loads successfully")
    public void pageReturns200(){
        assertEquals(200, response.status());
    }

    @Then("the page title is correct")
    public void checkPageTitle(){
        assertEquals("Get Legal Aid Data", page.title());
    }

    @Then("the heading is set")
    public void checkPageHeading(){
        var locator = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setLevel(1));
        assertEquals("Get Legal Aid Data", locator.textContent());
    }

    @Then("there is at least one report row")
    public void checkReportsExist(){
        var rows = page.locator(".govuk-grid-row");
        reportCount = rows.count();
        assertTrue(reportCount > 0);
    }

    @Then("there are download links")
    public void checkDownloadLinks(){
        var links = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("download"));
        var linkCount = links.count();
        // Should be as many download links as report rows
        assertEquals(reportCount, linkCount);
        assertTrue(links.first().getAttribute("href").matches(".*(csv|excel|file).*"));
    }

    @Then("the download links match the file format descriptor")
    public void checkDownloadLinksAndFormatDescriptor() {

        var links = page.getByRole(
                AriaRole.LINK,
                new Page.GetByRoleOptions().setName("Download report")
        );

        int count = links.count();

        for (int i = 0; i < count; i++) {
            var link = links.nth(i);

            String href = link.getAttribute("href");
            assertNotNull(href, "Download link href should not be null");

            // The visible file descriptor span (e.g. "(.xlsx file)")
            var descriptorSpan =
                    link.locator("xpath=following::span[@aria-hidden='true'][1]");

            String descriptorText = descriptorSpan.textContent().trim();

            if (href.contains("/excel")) {
                assertTrue(
                        descriptorText.contains("(.xlsx"),
                        "Expected XLSX descriptor for link: " + href
                );
            } else if (href.contains("/csv")) {
                assertTrue(
                        descriptorText.contains("(.csv"),
                        "Expected CSV descriptor for link: " + href
                );
            } else {
                fail("Unexpected download link format: " + href);
            }
        }
    }

    @Then("return 401 unauthorised")
    public void returnUnauthorised() {
        assertEquals(401, response.status());
    }

    @Then("there are no accessibility errors")
    public void doAccessibilityChecks() {
        // Gov minimum is WCAG 2.2 AA
        var ruleList = List.of(
                "wcag2a",
                "wcag2aa",
                "wcag21a",
                "wcag21aa",
                "wcag22aa",
                "best-practice"
        );
        var axeBuilder = new AxeBuilder(page).withTags(ruleList);

        var axeResults = axeBuilder.analyze();
        var incompleteTests = axeResults.getIncomplete();
        if (!incompleteTests.isEmpty()) {
            log.warn("Incomplete accessibility test count is {} and should be reviewed", incompleteTests.size());
            incompleteTests.forEach(rule -> log.warn("Incomplete accessibility test: {}", rule.getDescription()));
        }

        var violations = axeResults.getViolations();
        if (!violations.isEmpty()) {
            log.error("Accessibility test violation count is {}", violations.size());
            violations.forEach(rule -> log.error("Failed accessibility test: {}", rule.getDescription()));
        }
        assertEquals(0, violations.size());
    }

}
