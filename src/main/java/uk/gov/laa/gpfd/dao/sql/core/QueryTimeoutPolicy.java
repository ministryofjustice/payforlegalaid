package uk.gov.laa.gpfd.dao.sql.core;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * A {@link StatementConfigurationPolicy} implementation that sets the query timeout
 * for JDBC {@link PreparedStatement} objects.
 * <p>
 * The query timeout specifies the number of seconds the driver will wait for a statement
 * to execute before timing out. If the timeout expires, a {@link SQLException} is thrown.
 * </p>
 *
 *
 * <h3>Important Notes:</h3>
 * <ul>
 *   <li>A timeout value of 0 indicates no timeout (wait indefinitely)</li>
 *   <li>Timeout behavior is driver-dependent and not all drivers support this feature</li>
 *   <li>The timeout applies to both query execution and result set fetching</li>
 *   <li>Network timeouts may still occur independently of this setting</li>
 * </ul>
 *
 * @param timeoutSeconds the maximum number of seconds to wait for query execution.
 *                       Must be a non-negative integer (0 = no timeout).
 *
 * @see StatementConfigurationPolicy
 * @see PreparedStatement#setQueryTimeout(int)
 * @see java.sql.Statement#getQueryTimeout()
 * @see SQLException
 */
public record QueryTimeoutPolicy(int timeoutSeconds) implements StatementConfigurationPolicy {

    /**
     * Configures the query timeout on the provided {@link PreparedStatement}.
     * <p>
     * This implementation calls {@link PreparedStatement#setQueryTimeout(int)}
     * with the configured timeout value.
     * </p>
     *
     * @param ps the PreparedStatement to configure
     * @throws SQLException if a database access error occurs or the timeout value is invalid
     * @throws IllegalArgumentException if the statement is null
     * @throws IllegalStateException if the timeout value is negative
     */
    @Override
    public void configure(PreparedStatement ps) throws SQLException {
        if (ps == null) {
            throw new IllegalArgumentException("PreparedStatement cannot be null");
        }
        if (timeoutSeconds < 0) {
            throw new IllegalStateException("Timeout value cannot be negative");
        }
        ps.setQueryTimeout(timeoutSeconds);
    }
}
