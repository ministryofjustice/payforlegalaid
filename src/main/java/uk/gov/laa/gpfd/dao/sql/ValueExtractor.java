package uk.gov.laa.gpfd.dao.sql;

import org.springframework.jdbc.support.JdbcUtils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Objects;

import static org.springframework.jdbc.support.JdbcUtils.getResultSetValue;
import static org.springframework.jdbc.support.JdbcUtils.lookupColumnName;
import static uk.gov.laa.gpfd.dao.sql.ValueExtractor.HeaderValueExtractor;
import static uk.gov.laa.gpfd.dao.sql.ValueExtractor.RowValueExtractor;

/**
 * A sealed interface defining a strategy for extracting column values from database results.
 * Implementations provide specific extraction logic for different types of result data.
 */
public sealed interface ValueExtractor permits
        HeaderValueExtractor,
        RowValueExtractor
{
    /**
     * Extracts the value for the specified column index.
     *
     * @param columnIndex the 1-based column index to extract
     * @return the extracted value as a String
     * @throws SQLException if a database access error occurs
     */
    String extract(int columnIndex) throws SQLException;

    /**
     * Creates a ValueExtractor for extracting column headers from result set metadata.
     *
     * @param metaData the result set metadata containing column information
     * @return a new HeaderValueExtractor instance
     * @throws NullPointerException if metaData is null
     */
    static ValueExtractor ofHeader(ResultSetMetaData metaData) {
        Objects.requireNonNull(metaData, "ResultSetMetaData cannot be null");
        return new HeaderValueExtractor(metaData);
    }

    /**
     * Creates a ValueExtractor for extracting row data values from a result set.
     *
     * @param resultSet the result set containing row data
     * @return a new RowValueExtractor instance
     * @throws NullPointerException if resultSet is null
     */
    static ValueExtractor ofRow(ResultSet resultSet) {
        Objects.requireNonNull(resultSet, "ResultSet cannot be null");
        return new RowValueExtractor(resultSet);
    }

    /**
     * Implementation of ValueExtractor that extracts column names from result set metadata.
     *
     * @param metaData the result set metadata containing column names
     */
    record HeaderValueExtractor(ResultSetMetaData metaData) implements ValueExtractor {

        /**
         * {@inheritDoc}
         *
         * @implNote Delegates to {@link JdbcUtils#lookupColumnName(ResultSetMetaData, int)}
         *           to retrieve the column name
         */
        @Override
        public String extract(int columnIndex) throws SQLException {
            return lookupColumnName(metaData, columnIndex);
        }
    }

    /**
     * Implementation of ValueExtractor that extracts data values from a result set row.
     *
     * @param resultSet the result set containing the data row
     */
    record RowValueExtractor(ResultSet resultSet) implements ValueExtractor {
        private static final String EMPTY = "";

        /**
         * {@inheritDoc}
         *
         * @implNote Delegates to {@link JdbcUtils#getResultSetValue(ResultSet, int)}
         *           to retrieve the column value, converting null values to empty strings
         */
        @Override
        public String extract(int columnIndex) throws SQLException {
            Object value = getResultSetValue(resultSet, columnIndex);
            return null == value ? EMPTY: value.toString();
        }
    }
}
