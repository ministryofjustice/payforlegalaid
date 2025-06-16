package uk.gov.laa.gpfd.dao.sql.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.ResultSet.CONCUR_READ_ONLY;
import static java.sql.ResultSet.TYPE_FORWARD_ONLY;

/**
 * A {@link StatementCreationPolicy} implementation that creates forward-only, read-only
 * {@link PreparedStatement} objects.
 * <p>
 * This policy configures statements for optimal performance when streaming large result sets
 * sequentially. The created statements:
 * <ul>
 *   <li>Can only move forward through the result set ({@link ResultSet#TYPE_FORWARD_ONLY})</li>
 *   <li>Cannot be used to update data ({@link ResultSet#CONCUR_READ_ONLY})</li>
 * </ul>
 * </p>
 *
 * <h3>Performance Considerations:</h3>
 * <p>
 * Forward-only read-only statements typically provide the best performance for:
 * </p>
 * <ul>
 *   <li>Large result sets that are processed sequentially</li>
 *   <li>Read-only operations where result set scrolling isn't required</li>
 *   <li>Applications that can process data in a single pass</li>
 * </ul>
 *
 * @see StatementCreationPolicy
 * @see Connection#prepareStatement(String, int, int)
 * @see ResultSet#TYPE_FORWARD_ONLY
 * @see ResultSet#CONCUR_READ_ONLY
 */
public non-sealed class ForwardOnlyReadOnlyPolicy implements StatementCreationPolicy {

    /**
     * Creates a new forward-only, read-only prepared statement.
     *
     * @param conn the database connection to use for statement creation
     * @param sql the SQL query to prepare
     * @return a configured {@link PreparedStatement}
     * @throws SQLException if a database access error occurs or the SQL is invalid
     * @throws IllegalArgumentException if either connection or SQL parameters are null
     */
    @Override
    public PreparedStatement createStatement(Connection conn, String sql) throws SQLException {
        if (conn == null) {
            throw new IllegalArgumentException("Connection cannot be null");
        }
        if (sql == null || sql.trim().isEmpty()) {
            throw new IllegalArgumentException("SQL statement cannot be null or empty");
        }

        return conn.prepareStatement(
                sql,
                TYPE_FORWARD_ONLY,
                CONCUR_READ_ONLY
        );
    }
}
