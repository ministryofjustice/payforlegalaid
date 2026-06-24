package uk.gov.laa.pfla.hooks;

import io.cucumber.java.Scenario;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;

import java.util.function.Predicate;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

/**
 * A predicate implementation that determines whether a Cucumber scenario should be skipped
 * based on its file name matching certain criteria. This abstract class provides the foundation
 * for scenario skipping logic by implementing the {@link Predicate} interface for string matching.
 *
 * <p>The class is designed to be used as part of Cucumber test execution hooks to conditionally
 * skip scenarios based on their source file names.</p>
 */
abstract class SkipScenarioPredicate implements Predicate<String> {

    /**
     * The logger
     */
    private static final Logger logger = LoggerFactory.getLogger(SkipScenarioPredicate.class);

    /**
     * The lowercase representation of the scenario's source file path.
     */
    private final String scenarioSourceFilePath;

    /**
     * Private constructor that initializes the predicate with scenario file information.
     *
     * @param scenario The Cucumber scenario from which to derive the file path
     * @throws NullPointerException if the scenario or its URI is null
     */
    private SkipScenarioPredicate(Scenario scenario) {
        this.scenarioSourceFilePath = scenario.getUri().getSchemeSpecificPart().toLowerCase();
    }

    /**
     * Factory method that creates a new {@code SkipScenario} instance for the given scenario.
     *
     * @param scenario The Cucumber scenario to evaluate
     * @return A new {@code SkipScenario} predicate instance
     * @throws NullPointerException if the scenario parameter is null
     */
    public static SkipScenarioPredicate of(Scenario scenario) {
        return new SkipScenarioPredicate(scenario) {};
    }

    /**
     * Tests whether the scenario's file name contains the given string (case-insensitive).
     *
     * @param s The string to search for in the file name
     * @return {@code true} if the file name contains the given string (case-insensitive),
     * {@code false} otherwise
     * @throws NullPointerException if the input string is null
     */
    @Override
    public boolean test(String s) {
        requireNonNull(s, "Substring to match cannot be null - we're skip-happy, not reckless");

        var searchTerm = s.toLowerCase();

        logger.debug(() -> format("Checking scenario at %s for containing %s", scenarioSourceFilePath, searchTerm));

        var verdict = scenarioSourceFilePath.contains(searchTerm);

        if (verdict) {
            logger.info(() -> format("Scenario %s convicted of containing %s. Skipping...", scenarioSourceFilePath, searchTerm));
        }

        return verdict;
    }
}
