package uk.gov.laa.gpfd.services;

import org.springframework.jdbc.core.JdbcOperations;
import uk.gov.laa.gpfd.dao.JdbcDataStreamer;
import uk.gov.laa.gpfd.dao.stream.StreamingDao;
import uk.gov.laa.gpfd.model.Report;
import uk.gov.laa.gpfd.services.excel.ExcelCreationService;
import uk.gov.laa.gpfd.services.excel.editor.FormulaCalculator;
import uk.gov.laa.gpfd.services.excel.editor.PivotTableRefresher;
import uk.gov.laa.gpfd.services.excel.editor.SheetDataWriter;

import java.io.OutputStream;
import java.util.Map;

/**
 * Provides a contract for streaming data from various sources to an output destination.
 *
 * <p>
 * Implementations of this interface handle the transformation and streaming of data
 * from source systems (databases, APIs, etc.) to various output formats (CSV, Excel, etc.).
 * The interface supports different streaming strategies through factory methods.
 * </p>
 */
public interface DataStreamer {

    /**
     * Creates a new JDBC-based {@link DataStreamer} instance.
     * <p>
     * The returned implementation uses Spring's {@link JdbcOperations} to execute SQL queries
     * and stream results row-by-row to the output destination.
     *
     * @param jdbcOperations The configured JdbcOperations instance. Must not be null.
     * @return A ready-to-use JDBC data streamer
     * @throws IllegalArgumentException if jdbcTemplate is null
     * @see JdbcDataStreamer
     */
    static DataStreamer createJdbcStreamer(JdbcOperations jdbcOperations) {
        return new JdbcDataStreamer(jdbcOperations);
    }

    /**
     * Creates a new Excel-based {@link DataStreamer} instance.
     *
     * @param templateLoader      Service for loading Excel templates. Must not be null.
     * @param dataFetcher         DAO for streaming data from source systems. Must not be null.
     * @param sheetDataWriter     Component for writing data to Excel sheets. Must not be null.
     * @param pivotTableRefresher Component for refreshing pivot tables. Must not be null.
     * @param formulaCalculator   Component for calculating formulas. Must not be null.
     * @return A ready-to-use Excel data streamer implementation
     * @throws IllegalArgumentException if any argument is {@code null}
     * @see ExcelCreationService
     */
    static DataStreamer createExcelStreamer(TemplateService templateLoader,
                                            StreamingDao<Map<String, Object>> dataFetcher,
                                            SheetDataWriter sheetDataWriter,
                                            PivotTableRefresher pivotTableRefresher,
                                            FormulaCalculator formulaCalculator) {
        return new ExcelCreationService(templateLoader, dataFetcher, sheetDataWriter, pivotTableRefresher, formulaCalculator);
    }

    /**
     * Streams data from the specified query/command to the provided output stream.
     *
     * @param output The target output stream to write data to. Must not be null.
     * @throws IllegalArgumentException if query is null/empty or output is null
     */
    void stream(Report query, OutputStream output);
}

