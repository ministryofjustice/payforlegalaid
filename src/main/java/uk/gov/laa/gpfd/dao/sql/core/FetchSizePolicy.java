package uk.gov.laa.gpfd.dao.sql.core;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * A {@link StatementConfigurationPolicy} implementation that configures the fetch size
 * for JDBC {@link PreparedStatement} objects.
 * <p>
 * The fetch size determines the number of rows that are fetched from the database
 * at a time when executing a query. This policy allows for centralized control
 * of fetch size configuration across all statements.
 * </p>
 *
 *
 * @param fetchSize the number of rows to fetch in each round trip to the database.
 *                  Must be a positive integer. A value of 0 indicates the JDBC driver's
 *                  default fetch size should be used.
 *
 * @see StatementConfigurationPolicy
 * @see PreparedStatement#setFetchSize(int)
 * @see java.sql.Statement#getFetchSize()
 */
public record FetchSizePolicy(int fetchSize) implements StatementConfigurationPolicy {

    /**
     * Configures the fetch size on the provided {@link PreparedStatement}.
     *
     * @param ps the PreparedStatement to configure
     * @throws SQLException if a database access error occurs
     * @throws IllegalArgumentException if the statement is null
     */
    @Override
    public void configure(PreparedStatement ps) throws SQLException {
        if (ps == null) {
            throw new IllegalArgumentException("PreparedStatement cannot be null");
        }
        ps.setFetchSize(fetchSize);
    }
}
