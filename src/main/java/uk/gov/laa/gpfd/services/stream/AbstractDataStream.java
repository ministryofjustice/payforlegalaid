package uk.gov.laa.gpfd.services.stream;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import uk.gov.laa.gpfd.dao.ReportDao;
import uk.gov.laa.gpfd.enums.FileExtension;
import uk.gov.laa.gpfd.exception.ReportIdNotFoundException;
import uk.gov.laa.gpfd.services.DataStreamer;

import java.util.UUID;

import static java.util.Objects.requireNonNull;
import static uk.gov.laa.gpfd.enums.FileExtension.CSV;
import static uk.gov.laa.gpfd.enums.FileExtension.XLSX;
import static uk.gov.laa.gpfd.exception.TransferException.StreamException.ExcelStreamWriteException;

/**
 * Abstract base class for implementing {@link DataStream} with common response building functionality.
 * <p>
 * This class provides template methods and shared infrastructure for concrete streaming strategy
 * implementations. It handles common tasks like filename generation and response construction
 * while allowing subclasses to focus on format-specific streaming logic.
 * </p>
 *
 * <p><b>Key Features:</b>
 * <ul>
 *   <li>Pre-built response construction with proper headers</li>
 *   <li>Automatic filename generation with correct extensions</li>
 *   <li>Factory methods for standard strategy implementations</li>
 *   <li>Centralized error handling for streaming operations</li>
 * </ul>
 * </p>
 */
public abstract class AbstractDataStream implements DataStream {

    /**
     * Builds a complete streaming response with proper headers.
     * <p>
     * Automatically generates the filename by combining the base name with the
     * format-specific extension from {@link #getFormat()}.
     * </p>
     *
     * @param filenameBase the base name for the output file (without extension)
     * @param responseBody the streaming content to include in the response
     * @return fully constructed ResponseEntity with proper headers
     * @throws IllegalArgumentException if filenameBase is null or empty
     */
    ResponseEntity<StreamingResponseBody> buildResponse(String filenameBase, StreamingResponseBody responseBody) {
        requireNonNull(filenameBase, "Filename base cannot be null");
        var filename = String.format("%s.%s", filenameBase, getFormat().getExtension());

        return ResponseEntity.ok()
                .header("Content-Disposition", createContentDisposition(filename))
                .contentType(getContentType())
                .body(responseBody);
    }

    private String createContentDisposition(String filename) {
        return "attachment; filename=\"%s\"".formatted(sanitizeFilename(filename));
    }

    private String sanitizeFilename(String filename) {
        return filename.replace("\"", "");
    }

    /**
     * Creates a new CSV streaming strategy instance.
     *
     * @param reportDao the report data access object
     * @param dataStreamer the CSV data streaming component
     * @return a configured CSV streaming strategy
     * @throws IllegalArgumentException if any parameter is null
     */
    public static DataStream createCsvStreamStrategy(ReportDao reportDao, DataStreamer dataStreamer) {
        return new CsvDataStream(requireNonNull(reportDao), requireNonNull(dataStreamer));
    }

    /**
     * Creates a new Excel streaming strategy instance.
     *
     * @param reportDao the report data access object
     * @param dataStreamer the XLS data streaming component
     * @return a configured Excel streaming strategy
     * @throws IllegalArgumentException if any parameter is null
     */
    public static DataStream createExcelStreamStrategy(ReportDao reportDao, DataStreamer dataStreamer) {
        return new ExcelDataStream(requireNonNull(reportDao), requireNonNull(dataStreamer));
    }

    /**
     * CSV-specific streaming strategy implementation.
     * <p>
     * Handles conversion of report data to CSV format and streaming to the client.
     * </p>
     */
    @RequiredArgsConstructor
    static class CsvDataStream extends AbstractDataStream {
        private final ReportDao reportDao;
        private final DataStreamer dataStreamer;

        /**
         * {@inheritDoc}
         * @throws ReportIdNotFoundException if the requested report doesn't exist
         * @throws IllegalStateException if the report contains no queries
         */
        @Override
        public ResponseEntity<StreamingResponseBody> stream(UUID uuid) {
            var report = reportDao.fetchReportById(uuid)
                    .orElseThrow(() -> new ReportIdNotFoundException("Report with unrecognised ID "+ uuid.toString()));

            return buildResponse(report.getName(), output -> dataStreamer.stream(report, output));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public FileExtension getFormat() {
            return CSV;
        }
    }

    /**
     * Excel-specific streaming strategy implementation.
     * <p>
     * Handles generation of Excel workbooks and streaming to the client.
     * </p>
     */
    @RequiredArgsConstructor
    static class ExcelDataStream extends AbstractDataStream {
        private final ReportDao reportDao;
        private final DataStreamer dataStreamer;

        /**
         * {@inheritDoc}
         * @throws ExcelStreamWriteException if there's an error writing the Excel data
         */
        @Override
        public ResponseEntity<StreamingResponseBody> stream(UUID uuid) {
            var report = reportDao.fetchReportById(uuid)
                    .orElseThrow(() -> new ReportIdNotFoundException("Report not found for ID "+ uuid.toString()));

            return buildResponse(report.getName(), output -> dataStreamer.stream(report, output));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public FileExtension getFormat() {
            return XLSX;
        }
    }
}