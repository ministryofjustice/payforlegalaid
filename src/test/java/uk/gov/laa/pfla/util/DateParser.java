package uk.gov.laa.pfla.util;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.function.Function;

import static java.util.Optional.empty;

/**
 * A functional interface for parsing date strings into {@link Instant} objects.
 * Extends {@link Function} to allow functional composition, where the function
 * returns an {@link Optional} to safely handle parsing failures.
 *
 * @see DateTimeFormatter
 * @see Function
 */
@FunctionalInterface
public interface DateParser extends Function<String, Optional<Instant>> {

    /**
     * Parses the given date string into an {@link Instant}.
     *
     * @param date the date string to parse
     * @return the parsed {@link Instant}
     * @throws DateTimeParseException if the string cannot be parsed
     */
    Instant parse(String date) throws DateTimeParseException;

    /**
     * Functional interface implementation that parses the date string
     * and returns the result wrapped in an {@link Optional}.
     * Returns empty {@link Optional} if parsing fails.
     *
     * @param date the date string to parse
     * @return an {@link Optional} containing the parsed {@link Instant},
     *         or empty if parsing fails
     */
    @Override
    default Optional<Instant> apply(String date) {
        try {
            return Optional.of(parse(date));
        } catch (DateTimeParseException _) {
            return empty();
        }
    }

    /**
     * Creates a {@code DateParser} for the specified {@link DateTimeFormatter}.
     * The returned parser will use the formatter to parse dates into {@link Instant}s.
     *
     * @param formatter the formatter to use for parsing
     * @return a configured {@code DateParser}
     * @throws NullPointerException if formatter is null
     */
    static DateParser of(DateTimeFormatter formatter) {
        return date -> formatter.parse(date, Instant::from);
    }

    /**
     * Returns a {@code DateParser} configured for RFC 1123 date format.
     *
     * <p>Example format: {@code "Sun, 8 Dec 2024 12:10:00 GMT"}
     *
     * @return a shared {@code DateParser} instance for RFC 1123 format
     */
    static DateParser rfc1123() {
        return DateParser.of(DateTimeFormatter.RFC_1123_DATE_TIME);
    }

}