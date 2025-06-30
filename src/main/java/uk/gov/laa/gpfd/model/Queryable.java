package uk.gov.laa.gpfd.model;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Interface for types that contain queryable content with support for query processing.
 * <p>
 * Provides a flexible way to process and transform queries contained within implementing classes.
 * The interface includes default methods for common query operations and a built-in processor
 * for more complex query manipulation.
 * </p>
 *
 * @param <Q> The type of query objects contained in the implementing class
 * @param <T> The self-referential type of the implementing class
 */
public interface Queryable<Q extends Mapping, T extends Queryable<Q, T>> {

    /**
     * Gets the collection of queries contained in this object.
     *
     * @return a collection of query objects, may be empty but should not be null
     */
    Collection<Q> getQueries();

    /**
     * Extracts the first available query from this object as a ReportQuerySql.
     *
     * @return the first query as ReportQuerySql
     * @throws IllegalStateException if no queries are available
     */
    default ReportQuerySql extractFirstQuery() {
        @SuppressWarnings("unchecked")
        T self = (T) this;
        return Queryable.processor(self)
                .first()
                .orElseThrow(() -> new IllegalStateException("No queries available"));
    }

    /**
     * Extracts all mappings from the queries in this object.
     *
     * @return a collection of all mapping objects found in the queries
     */
    default Collection<Q> extractAllMappings() {
        @SuppressWarnings("unchecked")
        T self = (T) this;
        return Queryable.processor(self)
                .allMappings();
    }

    /**
     * Creates a new {@link QueryProcessor} for the given source object.
     *
     * @param <T> the type of the source object implementing Queryable
     * @param <Q> the type of query objects
     * @param source the source object to process queries from
     * @return a new QueryProcessor instance
     * @throws NullPointerException if source is null
     */
    static <T extends Queryable<Q, T>, Q extends Mapping> QueryProcessor<T, Q> processor(T source) {
        return new QueryProcessor<>(source) {};
    }

    /**
     * A processor for query objects that provides filtering, mapping and collection operations.
     *
     * @param <T> the type of the source object implementing Queryable
     * @param <Q> the type of query objects
     */
    abstract class QueryProcessor<T extends Queryable<Q, T>, Q extends Mapping> {
        private final T source;
        private Function<Q, ReportQuerySql> mapper;
        private Predicate<Q> filter = q -> true;

        /**
         * Creates a new QueryProcessor for the given source.
         *
         * @param source the source object containing queries to process
         * @throws NullPointerException if source is null
         */
        QueryProcessor(T source) {
            this.source = Objects.requireNonNull(source);
            this.mapper = Mapping::getQuery;
        }

        /**
         * Sets the mapping function to transform query objects.
         *
         * @param mapper the function to map query objects to ReportQuerySql
         * @return this processor for method chaining
         * @throws NullPointerException if mapper is null
         */
        public QueryProcessor<T, Q> mapping(Function<Q, ReportQuerySql> mapper) {
            this.mapper = Objects.requireNonNull(mapper);
            return this;
        }

        /**
         * Sets the filter predicate for query objects.
         *
         * @param filter the predicate to filter query objects
         * @return this processor for method chaining
         * @throws NullPointerException if filter is null
         */
        public QueryProcessor<T, Q> filtering(Predicate<Q> filter) {
            this.filter = Objects.requireNonNull(filter);
            return this;
        }

        /**
         * Gets the first query that matches the current filter, transformed by the current mapper.
         *
         * @return an Optional containing the first matching transformed query, or empty if none match
         */
        public Optional<ReportQuerySql> first() {
            return Optional.ofNullable(source.getQueries())
                    .stream()
                    .flatMap(Collection::stream)
                    .filter(filter)
                    .findFirst()
                    .map(mapper);
        }

        /**
         * Gets all queries that match the current filter, transformed by the current mapper.
         *
         * @return a list of all matching transformed queries
         */
        public List<ReportQuerySql> all() {
            return Optional.ofNullable(source.getQueries())
                    .stream()
                    .flatMap(Collection::stream)
                    .filter(filter)
                    .map(mapper)
                    .toList();
        }

        /**
         * Gets all queries that are instances of {@link Mapping} and match the current filter.
         *
         * @return a list of all matching Mapping objects
         */
        public List<Q> allMappings() {
            return Optional.ofNullable(source.getQueries())
                    .stream()
                    .flatMap(Collection::stream)
                    .filter(filter)
                    .toList();
        }
    }
}