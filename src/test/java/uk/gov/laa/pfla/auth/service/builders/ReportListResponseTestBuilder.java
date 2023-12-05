package uk.gov.laa.pfla.auth.service.builders;

import uk.gov.laa.pfla.auth.service.responses.ReportListResponse;

public class ReportListResponseTestBuilder {

    public static final int DEFAULT_ID = 1;
    public static final String DEFAULT_REPORT_NAME = "Excel_Report_Name-CSV-NAME-sheetnumber";
    private static final String DEFAULT_EXCEL_REPORT = "Excel_Report_Name";

    private static final String DEFAULT_CSV_NAME = "CSV-name";
    private static final int DEFAULT_EXCEL_SHEET_NUM = 7;
    public static final String DEFAULT_SQL_STRING = "SELECT * FROM SOMETHING";
    public static final String DEFAULT_BASE_URL = "www.sharepoint.com/the-folder-we're-using";
    public static final String DEFAULT_REPORT_OWNER = "Chancey Mctavish";
    public static final String DEFAULT_REPORT_CREATOR = "Barry Gibb";
    public static final String DEFAULT_REPORT_DESCRIPTION= "List all unpaid AP invoices and all outstanding AR debts at the end of the previous month. Detailed data, one row per invoice";

    public static final String DEFAULT_OWNER_EMAIL = "test-owner-email@example.com";

    private int id = DEFAULT_ID;
    private String reportName = DEFAULT_REPORT_NAME;
    private String excelReport = DEFAULT_EXCEL_REPORT ;
    private String csvName = DEFAULT_CSV_NAME;
    private int excelSheetNum = DEFAULT_EXCEL_SHEET_NUM;

    private String sqlQuery = DEFAULT_SQL_STRING;
    private String baseUrl = DEFAULT_BASE_URL;

    private String reportOwner = DEFAULT_REPORT_OWNER;
    private String reportCreator = DEFAULT_REPORT_CREATOR;
    private String description = DEFAULT_REPORT_DESCRIPTION;
    private String ownerEmail = DEFAULT_OWNER_EMAIL;


    public ReportListResponseTestBuilder withId(int id) {
        this.id = id;
        return this;
    }

    public ReportListResponseTestBuilder withExcelReport(String excelReport) {
        this.excelReport = excelReport;
        return this;
    }

    public ReportListResponseTestBuilder withReportName(String reportName) {
        this.reportName = reportName;
        return this;
    }

    public ReportListResponseTestBuilder withCsvName(String csvName) {
        this.csvName = csvName;
        return this;
    }

    public ReportListResponseTestBuilder withExcelSheetNumber(int excelSheetNum) {
        this.excelSheetNum = excelSheetNum;
        return this;
    }

    public ReportListResponseTestBuilder withSqlQuery(String sqlQuery) {
        this.sqlQuery = sqlQuery;
        return this;
    }

    public ReportListResponseTestBuilder withBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    public ReportListResponseTestBuilder withReportOwner(String reportOwner) {
        this.reportOwner = reportOwner;
        return this;
    }

    public ReportListResponseTestBuilder withReportCreator(String reportCreator) {
        this.reportCreator = reportCreator;
        return this;
    }

    public ReportListResponseTestBuilder withReportDescription(String description) {
        this.description = description;
        return this;
    }

    public ReportListResponseTestBuilder withOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
        return this;
    }

    public ReportListResponse createReportListResponse() {
        return new ReportListResponse(id, reportName, excelReport, csvName, excelSheetNum, sqlQuery, baseUrl, reportOwner, reportCreator, description, ownerEmail);
    }

}