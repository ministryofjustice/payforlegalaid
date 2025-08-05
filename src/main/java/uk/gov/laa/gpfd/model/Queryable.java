package uk.gov.laa.gpfd.model;

import uk.gov.laa.gpfd.model.excel.ExcelSheet;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toMap;

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
                .presentOnly()
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
                .filtering(e-> e.getQuery().isPresent())
                .allMappings();
    }

    /**
     * Retrieves the ordered mapping of Excel sheet names to their corresponding indices.
     * This implementation provides a convenient way to obtain sheet ordering information
     * while maintaining type safety. The returned map preserves the insertion order of sheets
     * as they appear in the original document.
     *
     * @return a {@link LinkedHashMap} where keys represent sheet names ({@link String})
     *         and values represent sheet indices ({@link Integer}), maintaining insertion order.
     */
    default LinkedHashMap<String, Integer> getSheetOrder() {
        @SuppressWarnings("unchecked")
        T self = (T) this;
        return Queryable.processor(self)
                .groupBy(Mapping::getExcelSheet)
                .apply(ExcelSheet::getName, ExcelSheet::getIndex);
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
         * Creates a predicate that tests whether a mapping contains a present query.
         *
         * @param <Q> the mapping type that must implement {@code getQuery()}
         * @return a predicate suitable for filtering mappings with present queries
         */
        public static <Q extends Mapping> Predicate<Q> hasPresentQuery() {
            return q -> {
                var query = q.getQuery();
                return query != null && query.isPresent();
            };
        }

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
         * Filters the processor to only include mappings that have a present (non-NONE) query.
         *
         * @return this processor configured to only process present queries, enabling method chaining
         */
        public QueryProcessor<T, Q> presentOnly() {
            return filtering(hasPresentQuery()::test);
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

        /**
         * Creates a grouping function that transforms query objects into an ordered map of Excel sheet properties.
         */
        public BiFunction<Function<ExcelSheet, String>, Function<ExcelSheet, Integer>, LinkedHashMap<String, Integer>> groupBy(
                Function<Q, ExcelSheet> key
        ) {
            Objects.requireNonNull(key, "Key extractor function cannot be null");

            var excelSheetStream = Optional.ofNullable(source.getQueries())
                    .stream()
                    .flatMap(Collection::stream)
                    .filter(filter)
                    .map(key)
                    .filter(Objects::nonNull);

            return (Function<ExcelSheet, String> keyFn, Function<ExcelSheet, Integer> valueFn) ->
                    excelSheetStream.filter(sheet -> {
                        var k = keyFn.apply(sheet);
                        var v = valueFn.apply(sheet);
                        return k != null && v != null;
                    }).collect(toMap(
                            keyFn,
                            valueFn,
                            (existing, replacement) -> existing,
                            LinkedHashMap::new
                    ));
        }
    }
}