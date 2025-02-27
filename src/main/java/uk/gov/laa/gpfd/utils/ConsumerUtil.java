package uk.gov.laa.gpfd.utils;

import java.util.function.Consumer;

/**
 * The {@code ConsumerUtil} interface provides utility methods for working with {@link Consumer} functional interfaces.
 *
 * <p>This interface is designed to simplify null-checking logic and promote cleaner, more concise code when working
 * with optional values and actions.
 */
public interface ConsumerUtil {

    /**
     * Conditionally applies the provided {@link Consumer} action if the specified value is not {@code null}.
     * This method is useful for avoiding explicit null checks and streamlining the application of optional actions.
     *
     * @param <T>    the type of the value to check
     * @param value  the value to check for nullability
     * @param action the {@link Consumer} action to apply if the value is not {@code null}
     */
    static <T> void applyIfPresent(T value, Consumer<T> action) {
        if (value != null) {
            action.accept(value);
        }
    }
}