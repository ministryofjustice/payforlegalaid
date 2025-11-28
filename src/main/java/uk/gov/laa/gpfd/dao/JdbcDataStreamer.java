package uk.gov.laa.gpfd.dao;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcOperations;
import uk.gov.laa.gpfd.config.AppConfig;
import uk.gov.laa.gpfd.model.Report;
import uk.gov.laa.gpfd.services.DataStreamer;

import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;

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
public record JdbcDataStreamer(JdbcOperations jdbc, AppConfig appConfig) implements DataStreamer {
    private static final char END_OF_LINE_SEPARATOR = '\n', EMPTY = ' ';

    /**
     * Streams the results of a SQL query directly to the provided output stream.
     *
     * @param report    The SQL query to execute (must not be {@code null} or empty)
     * @param stream The output stream to write results to (must not be {@code null})
     * @throws IllegalArgumentException if either argument is invalid
     * @throws RuntimeException        if database access or streaming fails
     * @implNote The caller is responsible for closing the output stream.
     */
    @Override
    public void stream(Report report, OutputStream stream) {
        if (null == report) {
            log.error("Null report provided for processing");
            throw new IllegalArgumentException("Report must not be null");
        }

        if (null == stream) {
            log.error("Null output stream");
            throw new IllegalArgumentException("Output stream must not be null");
        }

        stream(report.extractFirstQuery().value(), stream);
    }

    private void stream(String sql, OutputStream stream) {

        if (null == sql || sql.isBlank()) {
            log.error("Attempted to execute null/empty SQL query");
            throw new IllegalArgumentException("SQL query must not be null or empty");
        }

        Map<String, String> row = new LinkedHashMap<>();
        var csvMapper = new CsvMapper();

        log.debug("Initiating streaming for query: [{}]", sql.replace(END_OF_LINE_SEPARATOR, EMPTY));
        jdbc.query(sql, forStream(stream, csvMapper, row, appConfig.getCsvBufferFlushFrequency()));
        log.debug("Finished streaming for query: [{}]", sql.replace(END_OF_LINE_SEPARATOR, EMPTY));
    }
}
