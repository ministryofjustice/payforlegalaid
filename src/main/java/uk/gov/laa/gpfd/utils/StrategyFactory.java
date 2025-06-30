package uk.gov.laa.gpfd.utils;

import java.util.Collection;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;

import static java.util.Collections.unmodifiableMap;
import static java.util.Optional.ofNullable;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

/**
 * Interface for generic strategy factories that manage strategies keyed by a specific type.
 *
 * @param <K> the type of the key used to identify strategies
 * @param <S> the type of the strategy interface
 */
public sealed interface StrategyFactory<K, S> permits StrategyFactory.GenericStrategyFactory {

    /**
     * Creates a new {@link GenericStrategyFactory} instance with the given strategies and key extractor.
     * <p>
     *
     * @param <K> the enum type of keys used to identify strategies (must extend {@link Enum})
     * @param <S> the type of strategy implementations
     * @param strategyList collection of strategy implementations to be managed by the factory
     * @param keyExtractor function that extracts the key from a strategy instance
     * @return a new {@code GenericStrategyFactory} instance initialized with the given strategies
     * @throws IllegalArgumentException if {@code strategyList} or {@code keyExtractor} is null
     * @throws IllegalStateException if duplicate strategy keys are detected
     *
     * @see GenericStrategyFactory
     */
    static <K extends Enum<K>, S> GenericStrategyFactory<K, S> createGenericStrategyFactory(Collection<S> strategyList, Function<S, K> keyExtractor) {
        return new GenericStrategyFactory<K, S>(strategyList, keyExtractor);
    }

    /**
     * Gets all registered strategies.
     *
     * @return an unmodifiable map of all strategies
     */
    Map<K, S> getStrategies();

    /**
     * Gets a strategy by its key.
     *
     * @param key the key to look up
     * @return an Optional containing the strategy if found
     */
    Optional<S> getStrategy(K key);

    /**
     * Gets a strategy or throws an exception if not found.
     *
     * @param key the key to look up
     * @return the strategy implementation
     * @throws NoSuchElementException if no strategy exists for the key
     */
    S getRequiredStrategy(K key);

    /**
     * A generic factory for creating and retrieving strategies keyed by an arbitrary type.
     *
     * @param <K> the type of the key used to identify strategies (must be comparable)
     * @param <S> the type of the strategy interface
     */
    final class GenericStrategyFactory<K extends Enum<K>, S> implements StrategyFactory<K, S>{
        private final Map<K, S> strategies;

        /**
         * Creates a new GenericStrategyFactory.
         *
         * @param strategyList list of all available strategy implementations
         * @param keyExtractor function that extracts the key from a strategy
         */
        private GenericStrategyFactory(Collection<S> strategyList, Function<S, K> keyExtractor) {
            strategies = strategyList.stream().collect(toMap(keyExtractor, identity(), (S existing, S replacement) -> existing));
        }

        /**
         * Gets all registered strategies.
         *
         * @return an unmodifiable map of all strategies
         */
        public Map<K, S> getStrategies() {
            return unmodifiableMap(strategies);
        }

        /**
         * Gets a strategy by its key.
         *
         * @param key the key to look up
         * @return an Optional containing the strategy if found
         */
        public Optional<S> getStrategy(K key) {
            return ofNullable(strategies.get(key));
        }

        /**
         * Gets a strategy or throws an exception if not found.
         *
         * @param key the key to look up
         * @return the strategy implementation
         * @throws NoSuchElementException if no strategy exists for the key
         */
        public S getRequiredStrategy(K key) {
            return getStrategy(key).orElseThrow(() -> new NoSuchElementException("No strategy found for key: " + key));
        }
    }
}
