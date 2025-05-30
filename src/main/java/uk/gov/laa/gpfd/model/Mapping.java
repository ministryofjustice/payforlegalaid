package uk.gov.laa.gpfd.model;

import java.util.Collection;

/**
 * Defines a mapping between a data query, its presentation tab, and associated field attributes.
 *
 * @see ReportQuerySql
 * @see FieldAttributes
 */
public interface Mapping {
    /**
     * Gets the parameterized SQL query for data retrieval.
     * <p>
     * The query must follow strict formatting rules as defined by {@link ReportQuerySql},
     * including the use of parameter placeholders ({@code ?}) instead of literal values.
     *
     * @return the validated SQL query, never null
     */
    ReportQuerySql getQuery();

    /**
     * Gets the name of the tab sheet where the query results should be presented.
     *
     * @return the display name for the results, never {@code null} or blank
     */
    String getSheetName();

    /**
     * Gets the collection of field/column attributes that define how to process the results.
     *
     * @return an immutable collection of field attributes, never null.
     *         May be empty if no special processing is required.
     */
    Collection<FieldAttributes> getFieldAttributes();
}