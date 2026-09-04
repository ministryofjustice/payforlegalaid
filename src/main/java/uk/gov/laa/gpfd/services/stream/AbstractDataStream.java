package uk.gov.laa.gpfd.services.stream;

import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import uk.gov.laa.gpfd.dao.ReportDao;
import uk.gov.laa.gpfd.exception.ReportIdNotFoundException;
import uk.gov.laa.gpfd.model.FileExtension;
import uk.gov.laa.gpfd.model.Report;
import uk.gov.laa.gpfd.services.DataStreamer;

import java.util.UUID;

import static java.util.Objects.requireNonNull;
import static uk.gov.laa.gpfd.model.FileExtension.CSV;
import static uk.gov.laa.gpfd.model.FileExtension.XLSX;

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
@SuppressWarnings("java:S1118") // Abstract base class not a utility class
public abstract class AbstractDataStream implements DataStream {

    private final ReportDao reportDao;
    private final DataStreamer dataStreamer;
    private final FileExtension format;

    protected AbstractDataStream(ReportDao reportDao, DataStreamer dataStreamer, FileExtension format) {
        this.reportDao = reportDao;
        this.dataStreamer = dataStreamer;
        this.format = format;
    }

    @Override
    public StreamingResponseBody stream(UUID uuid) {
        var report = reportDao.fetchReportById(uuid)
                .orElseThrow(() -> new ReportIdNotFoundException(uuid));

        return stream(report);
    }

    @Override
    public StreamingResponseBody stream(Report report) {
        requireNonNull(report, "Report cannot be null");
        return output -> dataStreamer.stream(report, output);
    }

    @Override
    public FileExtension getFormat() {
        return format;
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
    static class CsvDataStream extends AbstractDataStream {

        CsvDataStream(ReportDao reportDao, DataStreamer dataStreamer) {
            super(reportDao, dataStreamer, CSV);
        }
    }

    /**
     * Excel-specific streaming strategy implementation.
     * <p>
     * Handles generation of Excel workbooks and streaming to the client.
     * </p>
     */
    static class ExcelDataStream extends AbstractDataStream {

        ExcelDataStream(ReportDao reportDao, DataStreamer dataStreamer) {
            super(reportDao, dataStreamer, XLSX);
        }
    }
}
