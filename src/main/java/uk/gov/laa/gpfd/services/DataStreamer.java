package uk.gov.laa.gpfd.services;

import org.springframework.jdbc.core.JdbcTemplate;
import uk.gov.laa.gpfd.dao.JdbcDataStreamer;

import java.io.OutputStream;

/**
 * Provides a contract for streaming data from various sources to an output destination.
 */
public interface DataStreamer {

    /**
     * Streams data from the specified query/command to the provided output stream.
     *
     * @param output The target output stream to write data to. Must not be null.
     * @throws IllegalArgumentException if query is null/empty or output is null
     */
    void stream(String query, OutputStream output);

    /**
     * Creates a new JDBC-based {@link DataStreamer} instance.
     * <p>
     * The returned implementation uses Spring's {@link JdbcTemplate} to execute SQL queries
     * and stream results row-by-row to the output destination.
     *
     * @param jdbcTemplate The configured JdbcTemplate instance. Must not be null.
     * @return A ready-to-use JDBC data streamer
     * @throws IllegalArgumentException if jdbcTemplate is null
     * @see JdbcDataStreamer
     */
    static DataStreamer createJdbcStreamer(JdbcTemplate jdbcTemplate) {
        return new JdbcDataStreamer(jdbcTemplate);
    }
}

