package uk.gov.laa.gpfd.dao;

import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowCallbackHandler;
import uk.gov.laa.gpfd.model.Mapping;

/**
 * Abstract base class for streaming JDBC query results to an Excel workbook sheet.
 * <p>
 * This class provides a framework for executing SQL queries and streaming the results
 * to an Apache POI {@link Sheet}. The streaming is performed efficiently with forward-only,
 * read-only result sets and configurable fetch size for optimal memory usage with large datasets.
 */
public abstract class JdbcWorkbookDataStreamer {

    protected final JdbcOperations jdbcOperations;

    /**
     * Constructs a new JdbcWorkbookDataStreamer with the specified JDBC operations template.
     *
     * @param jdbcOperations the Spring JDBC operations template (must not be null)
     */
    public JdbcWorkbookDataStreamer(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    /**
     * Executes a query and streams the results to the specified worksheet.
     * <p>
     * The query is constructed using the provided mapping configuration, and results
     * are streamed row-by-row to minimize memory usage.
     *
     * @param sheet   the target worksheet to receive the data (must not be null)
     * @param mapping the configuration mapping for query generation (must not be null)
     * @throws IllegalArgumentException if either parameter is null
     */
    public final void queryToSheet(Sheet sheet, Mapping mapping) {
        var psc = createStatementCreator(getSql(mapping));
        var rch = createRowCallbackHandler(sheet, mapping);

        jdbcOperations.query(psc, rch);
    }

    /**
     * Generates the SQL query to execute based on the provided mapping.
     * <p>
     * Implementing classes must provide the SQL generation logic appropriate
     * for their specific requirements.
     *
     * @param mapping the configuration mapping for query generation
     * @return the SQL query string
     */
    protected abstract String getSql(Mapping mapping);

    /**
     * Creates a prepared statement creator with optimal settings for streaming.
     * <p>
     * Configures the statement with:
     * <ul>
     *   <li>Forward-only result set type</li>
     *   <li>Read-only concurrency mode</li>
     *   <li>Large fetch size (1000 rows) for efficient streaming</li>
     * </ul>
     *
     * @param sql the SQL query to prepare
     * @return a configured PreparedStatementCreator
     */
    protected abstract PreparedStatementCreator createStatementCreator(String sql);

    /**
     * Creates a row callback handler to process result set rows.
     *
     * @param sheet   the target worksheet
     * @param mapping the configuration mapping
     * @return a configured RowCallbackHandler
     */
    protected abstract RowCallbackHandler createRowCallbackHandler(Sheet sheet, Mapping mapping);

}
