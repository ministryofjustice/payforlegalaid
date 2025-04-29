package uk.gov.laa.gpfd.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcOperations;
import uk.gov.laa.gpfd.services.DataStreamer;

import java.io.OutputStream;

import static uk.gov.laa.gpfd.dao.sql.ChannelRowHandler.forStream;

/**
 * A lightweight DAO for streaming SQL query results directly to an {@link OutputStream}.
 * <p>
 * Designed for efficient handling of large datasets by avoiding memory accumulation.
 * The streaming starts immediately upon query execution.
 *
 * @implNote Uses Spring's {@link JdbcOperations} for database operations and a
 *           row-by-row callback mechanism for streaming.
 */
@Slf4j
public record JdbcDataStreamer(JdbcOperations jdbc) implements DataStreamer {
    private static final char END_OF_LINE_SEPARATOR = '\n', EMPTY = ' ';

    /**
     * Streams the results of a SQL query directly to the provided output stream.
     *
     * @param sql    The SQL query to execute (must not be {@code null} or empty)
     * @param stream The output stream to write results to (must not be {@code null})
     * @throws IllegalArgumentException if either argument is invalid
     * @throws RuntimeException        if database access or streaming fails
     * @implNote The caller is responsible for closing the output stream.
     */
    @Override
    public void stream(String sql, OutputStream stream) {
        if (null == sql || sql.isBlank()) {
            log.error("Attempted to execute null/empty SQL query");
            throw new IllegalArgumentException("SQL query must not be null or empty");
        }
        if (null == stream) {
            log.error("Null output stream provided for query: {}", sql);
            throw new IllegalArgumentException("Output stream must not be null");
        }

        log.debug("Initiating streaming for query: [{}]", sql.replace(END_OF_LINE_SEPARATOR, EMPTY));
        jdbc.query(sql, forStream(stream));
        log.debug("Finished streaming for query: [{}]", sql.replace(END_OF_LINE_SEPARATOR, EMPTY));
    }
}
