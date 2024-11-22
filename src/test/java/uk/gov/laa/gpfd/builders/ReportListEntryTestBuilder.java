package uk.gov.laa.gpfd.builders;


import uk.gov.laa.gpfd.model.ReportsGet200ResponseReportListInner;

public class ReportListEntryTestBuilder {

    public static final int DEFAULT_ID = 1;
    public static final String DEFAULT_REPORT_NAME = "Excel_Report_Name-CSV-NAME-sheetnumber";
    private static final String DEFAULT_EXCEL_REPORT = "Excel_Report_Name";

    private static final String DEFAULT_CSV_NAME = "CSV-name";
    private static final int DEFAULT_EXCEL_SHEET_NUM = 7;
    public static final String DEFAULT_SQL_STRING = "SELECT * FROM SOMETHING";
    public static final String DEFAULT_BASE_URL = "www.sharepoint.com/folder-for-storing-created-reports";
    public static final String DEFAULT_REPORT_OWNER = "Chancey Mctavish";
    public static final String DEFAULT_REPORT_CREATOR = "Barry Gibb";
    public static final String DEFAULT_REPORT_DESCRIPTION = "List all unpaid AP invoices and all outstanding AR debts at the end of the previous month. Detailed data, one row per invoice";

    public static final String DEFAULT_OWNER_EMAIL = "test-owner-email@example.com";

    private int id = DEFAULT_ID;
    private String reportName = DEFAULT_REPORT_NAME;
    private String excelReport = DEFAULT_EXCEL_REPORT;
    private String csvName = DEFAULT_CSV_NAME;
    private int excelSheetNum = DEFAULT_EXCEL_SHEET_NUM;

    private String sqlQuery = DEFAULT_SQL_STRING;
    private String baseUrl = DEFAULT_BASE_URL;

    private String reportOwner = DEFAULT_REPORT_OWNER;
    private String reportCreator = DEFAULT_REPORT_CREATOR;
    private String description = DEFAULT_REPORT_DESCRIPTION;
    private String ownerEmail = DEFAULT_OWNER_EMAIL;


    public ReportListEntryTestBuilder withId(int id) {
        this.id = id;
        return this;
    }

    public ReportListEntryTestBuilder withExcelReport(String excelReport) {
        this.excelReport = excelReport;
        return this;
    }

    public ReportListEntryTestBuilder withReportName(String reportName) {
        this.reportName = reportName;
        return this;
    }

    public ReportListEntryTestBuilder withBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    public ReportsGet200ResponseReportListInner createReportListResponse() {
        return new ReportsGet200ResponseReportListInner(){{
            id(id);
            reportName(reportName);
            excelReport(excelReport);
            csvName(csvName);
            excelSheetNum(excelSheetNum);
            sqlQuery(sqlQuery);
            baseUrl(baseUrl); reportOwner(reportOwner); reportCreator(reportCreator); description(description); ownerEmail(ownerEmail);
        }};
    }

}