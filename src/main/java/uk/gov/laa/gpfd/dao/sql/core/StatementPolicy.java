package uk.gov.laa.gpfd.dao.sql.core;

import org.springframework.jdbc.core.PreparedStatementCreator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static java.sql.ResultSet.CONCUR_READ_ONLY;
import static java.sql.ResultSet.TYPE_FORWARD_ONLY;

/**
 * A composite policy class that combines both statement creation and configuration.
 */
public class StatementPolicy {

    private final int fetchSize;
    private final int timeout;

    public StatementPolicy(int fetchSize, int timeout) {
        if (timeout < 0) {
            throw new IllegalStateException("Timeout value cannot be negative");
        }
        this.fetchSize = fetchSize;
        this.timeout = timeout;
    }

    /**
     * Creates a {@link PreparedStatementCreator} that applies both creation and configuration policies.
     *
     * @param sql the SQL query to be executed (must not be null or empty)
     * @return a prepared statement creator that applies the full policy chain
     * @throws IllegalArgumentException if the SQL parameter is null or empty
     */
    public PreparedStatementCreator createStatementCreator(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            throw new IllegalArgumentException("SQL statement cannot be null or empty");
        }

        return (Connection conn) -> {
            PreparedStatement ps = createStatement(conn, sql);
            configure(ps);
            return ps;
        };
    }

    private PreparedStatement createStatement(Connection conn, String sql) throws SQLException {
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

    private void configure(PreparedStatement ps) throws SQLException {
        ps.setFetchSize(fetchSize);
        ps.setQueryTimeout(timeout);
    }
}

