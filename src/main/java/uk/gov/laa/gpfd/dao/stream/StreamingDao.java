package uk.gov.laa.gpfd.dao.stream;

import org.springframework.dao.DataAccessException;
import uk.gov.laa.gpfd.model.ReportQuerySql;

import java.util.stream.Stream;

/**
 * A generic DAO (Data Access Object) interface for streaming data from a data source.
 * <p>
 * Provides methods to execute queries and return results as a {@link Stream} of entities,
 * enabling efficient processing of large result sets without loading all data into memory.
 * </p>
 */
public interface StreamingDao<T> {

    /**
     * Executes a SQL query and returns the results as a stream.
     *
     * @param sql the SQL query to execute (must not be {@code null} or empty)
     * @return a {@link Stream} of mapped results (never {@code null})
     * @throws IllegalArgumentException if sql is {@code null} or empty
     * @throws DataAccessException      if there is any problem executing the query
     */
    Stream<T> queryForStream(ReportQuerySql sql) throws DataAccessException;

    /**
     * Executes a parameterized SQL query and returns the results as a stream.
     *
     * @param sql    the SQL query containing ? placeholders (must not be {@code null} or empty)
     * @param params the parameters to bind to the query (may be {@code null} if no parameters)
     * @return a {@link Stream} of mapped results (never {@code null})
     * @throws IllegalArgumentException if sql is {@code null} or empty
     * @throws DataAccessException      if there is any problem executing the query
     */
    Stream<T> queryForStream(ReportQuerySql sql, Object... params) throws DataAccessException;
}