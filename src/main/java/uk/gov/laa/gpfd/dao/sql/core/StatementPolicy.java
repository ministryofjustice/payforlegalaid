package uk.gov.laa.gpfd.dao.sql.core;

import org.springframework.jdbc.core.PreparedStatementCreator;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * A composite policy interface that combines both statement creation and configuration capabilities.
 */
public non-sealed interface StatementPolicy
        extends StatementCreationPolicy, StatementConfigurationPolicy {

    /**
     * Creates a {@link PreparedStatementCreator} that applies both creation and configuration policies.
     *
     * @param sql the SQL query to be executed (must not be null or empty)
     * @return a prepared statement creator that applies the full policy chain
     * @throws IllegalArgumentException if the SQL parameter is null or empty
     */
    default PreparedStatementCreator createStatementCreator(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            throw new IllegalArgumentException("SQL statement cannot be null or empty");
        }

        return (Connection conn) -> {
            PreparedStatement ps = createStatement(conn, sql);
            configure(ps);
            return ps;
        };
    }
}
