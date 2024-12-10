package uk.gov.laa.gpfd.data;

import uk.gov.laa.gpfd.model.ReportsGet200ResponseReportListInner;

/**
 * Factory class to generate test data for instances of {@link ReportsGet200ResponseReportListInner}.
 * This class provides methods to generate valid instances of {@link ReportsGet200ResponseReportListInner}
 * with default or custom data, which can be useful for unit tests, mock data generation, or other testing purposes.
 * <p>
 * The generated instances represent a report list entry and include common report details such as report ID,
 * report name, report owner, SQL query, and other metadata.
 * </p>
 */
public class ReportListEntryTestDataFactory {

    /**
     * Generates a valid {@link ReportsGet200ResponseReportListInner} instance with default values.
     * <p>
     * The default values set in this method simulate a typical report list entry, including report ID,
     * report name, owner, description, and other details about the report.
     * </p>
     *
     * @return A new instance of {@link ReportsGet200ResponseReportListInner} with valid default data.
     */
    public static ReportsGet200ResponseReportListInner aValidReportsGet200ResponseReportListInner() {
        return new ReportsGet200ResponseReportListInner() {{
            id(1);
            reportName("Excel_Report_Name-CSV-NAME-sheetnumber");
            description("List all unpaid AP invoices and all outstanding AR debts at the end of the previous month. Detailed data, one row per invoice");
        }};
    }

    /**
     * Generates a valid {@link ReportsGet200ResponseReportListInner} instance with a custom report ID.
     * <p>
     * This method allows customization of the report ID, while the other fields remain the same as in the default version.
     * This can be useful when you need to test reports with different IDs but otherwise identical data.
     * </p>
     *
     * @param id The custom report ID to assign to the generated report entry.
     * @return A new instance of {@link ReportsGet200ResponseReportListInner} with the specified ID and valid default data.
     */
    public static ReportsGet200ResponseReportListInner aValidReportsGet200ResponseReportListInnerWithCustomId(int id) {
        return new ReportsGet200ResponseReportListInner() {{
            id(id);
            reportName("Excel_Report_Name-CSV-NAME-sheetnumber");
            description("List all unpaid AP invoices and all outstanding AR debts at the end of the previous month. Detailed data, one row per invoice");
        }};
    }
}