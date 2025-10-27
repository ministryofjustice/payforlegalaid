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
        return stream(ReportType.values()).map(ReportType::getReportData);
    }

    /**
     * Generates the expected download URL for this report.
     *
     * @return formatted URL string following the pattern: <a href="http://localhost/">...</a>{type-subpath}/{id}
     */
    public String expectedUrl() {
        if (fileType.equals(FileExtension.S3STORAGE)) {
            return "http://localhost/%s/%s/%s".formatted("reports", id, "file");
        }
        return "http://localhost/%s/%s".formatted(fileType.getSubPath(), id);
    }

    /**
     * Enumeration of standard report test data instances.
     * Each enum constant represents a specific report type with its test data.
     */
    public enum ReportType {
        CSV_REPORT("0d4da9ec-b0b3-4371-af10-f375330d85d3", "CIS to CCMS payment value Defined", CSV),
        CCMS_REPORT("b36f9bbb-1178-432c-8f99-8090e285f2d3", "CCMS Invoice Analysis (CIS to CCMS)", XLSX),
        GENERAL_LEDGER_REPORT("f46b4d3d-c100-429a-bf9a-223305dbdbfb", "CCMS General ledger extractor (small manual batches)", XLSX),
        CCMS_AND_CIS_BANK_ACCOUNT_REPORT("eee30b23-2c8d-4b4b-bb11-8cd67d07915c", "CCMS and CIS Bank Account Report w Category Code (YTD)", XLSX),
        LEGAL_HELP_CONTRACT_BALANCES_REPORT("7073dd13-e325-4863-a05c-a049a815d1f7", "Legal Help contract balances", XLSX),
        AGFS_LATE_PROCESSED_BILLS_REPORT("7bda9aa4-6129-4c71-bd12-7d4e46fdd882", "AGFS late processed bills", XLSX),
        CCMS_THIRD_PARTY_REPORT("8b9f0484-819f-4e0f-b60a-0b3f9d30d9ba", "CCMS Third party report", XLSX),
        REP012ID("cc55e276-97b0-4dd8-a919-26d4aa373266", "REP012 - Original Submissions Value Report", S3STORAGE);

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