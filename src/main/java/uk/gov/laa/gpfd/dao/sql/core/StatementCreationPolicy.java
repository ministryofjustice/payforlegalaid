package uk.gov.laa.gpfd.dao.sql.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * A policy interface for creating configured {@link PreparedStatement} objects.
 * <p>
 * Implementations of this interface encapsulate the logic for creating JDBC prepared statements
 * with specific characteristics such as result set type, concurrency mode, or other
 * statement-level configurations that must be set at creation time.
 * </p>
 *
 * @see PreparedStatement
 * @see Connection#prepareStatement(String, int, int)
 * @see StatementConfigurationPolicy
 * @see StatementPolicy
 */
public sealed interface StatementCreationPolicy permits
        ForwardOnlyReadOnlyPolicy,
        StatementPolicy {

    /**
     * Creates a new prepared statement with the policy's specific configuration.
     *
     * @param conn the database connection to use for statement creation (must not be null)
     * @param sql  the SQL query to prepare (must not be null or empty)
     * @return a newly created {@link PreparedStatement} with the policy's configuration applied
     * @throws SQLException             if a database access error occurs or the SQL is invalid
     * @throws IllegalArgumentException if either connection or SQL parameters are invalid
     */
    PreparedStatement createStatement(Connection conn, String sql) throws SQLException;
}
