package uk.gov.laa.pfla.hooks;

import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.junit.platform.commons.logging.Logger;
import uk.gov.laa.pfla.configuration.PlaywrightManager;

import static java.lang.System.getenv;
import static java.util.stream.Stream.of;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.platform.commons.logging.LoggerFactory.getLogger;
import static org.junit.platform.commons.util.StringUtils.isBlank;

/**
 * A class containing hook methods that execute before or after scenarios in a test framework.
 * This class provides functionality to conditionally skip scenarios based on configuration.
 */
public class Hooks {

    /**
     * The logger
     */
    private static final Logger logger = getLogger(SkipScenarioPredicate.class);

    private static final String SPLITTER = ",";


    /**
     * Ensure if any Playwright is hanging around it is cleaned up to avoid memory errors
     */
    @AfterAll
    public static void afterAll(){
        PlaywrightManager.closePlaywright();
    }

    /**
     * Executes before each scenario and checks if the scenario should be skipped based on configuration.
     * If the scenario's name matches any of the disabled tests specified in {@code testsToDisable},
     * the scenario will be skipped with an appropriate message.
     */
    @Before
    public void beforeScenario(Scenario scenario) {
        var testsToDisable = getenv("TESTS_TO_DISABLE");
        if (isBlank(testsToDisable)) {
            logger.trace(() -> "No tests to disable - proceeding with all scenarios");
            return;
        }

        var skipScenario = SkipScenarioPredicate.of(scenario);
        var shouldSkip = of(testsToDisable.split(SPLITTER)).anyMatch(skipScenario);

        if (shouldSkip) {
            var skipMessage = "Skipping scenario '" + scenario.getName() + "' (matched pattern)";
            logger.info(() -> skipMessage);
            scenario.log(skipMessage);
            assumeFalse(true, "Test disabled by config");
        } else {
            logger.debug(() -> "No pattern matches - executing scenario");
        }
    }

}
