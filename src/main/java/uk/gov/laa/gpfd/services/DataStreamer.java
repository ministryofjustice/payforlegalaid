package uk.gov.laa.gpfd.services;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.jdbc.core.JdbcOperations;
import uk.gov.laa.gpfd.dao.JdbcDataStreamer;
import uk.gov.laa.gpfd.dao.JdbcWorkbookDataStreamer;
import uk.gov.laa.gpfd.exception.TemplateResourceException;
import uk.gov.laa.gpfd.model.Report;
import uk.gov.laa.gpfd.services.excel.ExcelCreationService;
import uk.gov.laa.gpfd.services.excel.editor.FormulaCalculator;
import uk.gov.laa.gpfd.services.excel.editor.PivotTableRefresher;
import uk.gov.laa.gpfd.services.excel.formatting.CellFormatter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

import static uk.gov.laa.gpfd.exception.TemplateResourceException.ExcelTemplateCreationException;

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
     * Creates a configured Excel {@link DataStreamer} instance with all required dependencies.
     *
     * @param templateLoader      Service for loading Excel templates (required)
     * @param dataFetcher         DAO for streaming data from source systems (required)
     * @param pivotTableRefresher Component for refreshing pivot tables (required)
     * @param formulaCalculator   Component for calculating formulas (required)
     * @param formatter           Component for formatting cells (required)
     * @return A fully configured Excel data streamer implementation
     * @throws IllegalArgumentException if any argument is null
     */
    static DataStreamer createExcelStreamer(
            TemplateService templateLoader,
            JdbcWorkbookDataStreamer dataFetcher,
            PivotTableRefresher pivotTableRefresher,
            FormulaCalculator formulaCalculator,
            CellFormatter formatter
    ) {
        Objects.requireNonNull(templateLoader, "Template service must not be null");
        Objects.requireNonNull(dataFetcher, "Data fetcher must not be null");
        Objects.requireNonNull(pivotTableRefresher, "Pivot table refresher must not be null");
        Objects.requireNonNull(formulaCalculator, "Formula calculator must not be null");
        Objects.requireNonNull(formatter, "Cell formatter must not be null");

        return new ExcelCreationService(
                templateLoader,
                dataFetcher,
                pivotTableRefresher,
                formulaCalculator,
                formatter
        );
    }

    /**
     * Streams data from the specified query/command to the provided output stream.
     *
     * @param output The target output stream to write data to. Must not be null.
     * @throws IllegalArgumentException if query is null/empty or output is null
     */
    void stream(Report query, OutputStream output);

    interface WorkbookDataStreamer extends DataStreamer {

        /**
         * Streams data from the db into the target workbook, applying all mappings
         * and transformations defined in the report.
         *
         * @param report   The report definition containing data and mapping configuration
         * @param workbook The target workbook to be populated
         * @throws IllegalArgumentException if either argument is null
         */
        void stream(Report report, Workbook workbook);

        /**
         * Streams data from the db directly to an output stream,
         * automatically handling template loading and resource management.
         *
         * @param report The report definition to execute
         * @param output The target output stream for the generated workbook
         * @throws TemplateResourceException if the template cannot be loaded
         * @throws IllegalStateException     if there's an error writing to the stream
         * @throws IllegalArgumentException  if either argument is null
         */
        default void stream(Report report, OutputStream output) {
            Objects.requireNonNull(report, "Report must not be null");
            Objects.requireNonNull(output, "Output stream must not be null");

            try (var workbook = resolveTemplate(report)) {
                stream(report, workbook);
                workbook.write(output);
            } catch (IOException e) {
                throw new ExcelTemplateCreationException(e, "Failed to generate Excel report '%s'", report.getName());
            }
        }

        /**
         * Resolves the appropriate template workbook for the given report.
         * Implementations should override this if they need custom template resolution logic.
         *
         * @param report The report requesting the template
         * @return A loaded workbook template
         * @throws TemplateResourceException if the template cannot be loaded
         */
        Workbook resolveTemplate(Report report);
    }
}

