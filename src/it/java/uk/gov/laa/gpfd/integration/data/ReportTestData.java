package uk.gov.laa.gpfd.integration.data;

import uk.gov.laa.gpfd.model.FileExtension;

import java.util.stream.Stream;

import static java.util.Arrays.stream;
import static uk.gov.laa.gpfd.model.FileExtension.CSV;
import static uk.gov.laa.gpfd.model.FileExtension.XLSX;
import static uk.gov.laa.gpfd.model.FileExtension.S3STORAGE;

/**
 * Immutable record representing test data for report-related test cases.
 * <p>
 * Provides standardized test data including report identifiers, names, and file types,
 * along with utility methods for test operations. Predefined report instances are
 * available through the {@link ReportType} enum.
 * </p>
 *
 * @param id       the unique identifier for the report (typically a UUID string)
 * @param name     the human-readable name/description of the report
 * @param fileType the file extension/format type of the report
 */
public record ReportTestData(
        String id,
        String name,
        FileExtension fileType
) {

    /**
     * Returns a stream of all predefined ReportTestData instances.
     *
     * @return stream of all enum-defined ReportTestData instances
     */
    public static Stream<ReportTestData> getAllTestReports() {
        return Stream.of(ReportType.REP000ID.getReportData());
    }

    /**
     * Generates the expected download URL for this report.
     *
     * @return formatted URL string following the pattern: http://localhost/reports/{id}/{subpath}
     */
    public String expectedUrl() {
        if (fileType.equals(FileExtension.S3STORAGE)) {
            return "http://localhost/reports/%s/file".formatted(id);
        }
        return "http://localhost/reports/%s/%s".formatted(id, fileType.getSubPath());
    }

    /**
     * Enumeration of standard report test data instances.
     * Each enum constant represents a specific report type with its test data.
     */
    public enum ReportType {
        CSV_REPORT("f46b4d3d-c100-429a-bf9a-6c3305dbdbf4", "CSV generated report", CSV),
        REP012ID("cc55e276-97b0-4dd8-a919-26d4aa373266", "REP012 - Original Submissions Value Report", S3STORAGE),
        REP000ID("523f38f0-2179-4824-b885-3a38c5e149e8", "REP000 - Combined Data Extract for Submit a Bulk Claim Data", S3STORAGE);

        private final ReportTestData reportData;

        ReportType(String id, String name, FileExtension fileType) {
            this.reportData = new ReportTestData(id, name, fileType);
        }

        /**
         * Gets the ReportTestData instance for this enum value.
         *
         * @return the immutable ReportTestData instance
         */
        public ReportTestData getReportData() {
            return reportData;
        }
    }
}