package uk.gov.laa.pfla.auth.service.builders;

import uk.gov.laa.pfla.auth.service.responses.ReportListResponse;

public class ReportListResponseBuilder {


    private int id;
    private String reportName;
    private String excelReport;
    private String csvName;
    private int excelSheetNum;
    private String sqlQuery;
    private String baseUrl;
    private String reportOwner;
    private String reportCreator;
    private String description;
    private String ownerEmail;

    public ReportListResponseBuilder(int id, String reportName, String excelReport, String csvName, int excelSheetNum, String sqlQuery, String baseUrl, String reportOwner, String reportCreator, String description, String ownerEmail) {
        this.id = id;
        this.reportName = reportName;
        this.excelReport = excelReport;
        this.csvName = csvName;
        this.excelSheetNum = excelSheetNum;
        this.sqlQuery = sqlQuery;
        this.baseUrl = baseUrl;
        this.reportOwner = reportOwner;
        this.reportCreator = reportCreator;
        this.description = description;
        this.ownerEmail = ownerEmail;
    }

    public ReportListResponseBuilder withId(int id) {
        this.id = id;
        return this;
    }

    public ReportListResponseBuilder withExcelReport(String excelReport) {
        this.excelReport = excelReport;
        return this;
    }

    public ReportListResponseBuilder withReportName(String reportName) {
        this.reportName = reportName;
        return this;
    }

    public ReportListResponseBuilder withCsvName(String csvName) {
        this.csvName = csvName;
        return this;
    }

    public ReportListResponseBuilder withExcelSheetNumber(int excelSheetNum) {
        this.excelSheetNum = excelSheetNum;
        return this;
    }

    public ReportListResponseBuilder withSqlQuery(String sqlQuery) {
        this.sqlQuery = sqlQuery;
        return this;
    }

    public ReportListResponseBuilder withBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    public ReportListResponseBuilder withReportOwner(String reportOwner) {
        this.reportOwner = reportOwner;
        return this;
    }

    public ReportListResponseBuilder withReportCreator(String reportCreator) {
        this.reportCreator = reportCreator;
        return this;
    }

    public ReportListResponseBuilder withReportDescription(String description) {
        this.description = description;
        return this;
    }

    public ReportListResponseBuilder withOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
        return this;
    }

    public ReportListResponse createReportListResponse() {
        return new ReportListResponse(id, reportName, excelReport, csvName, excelSheetNum, sqlQuery, baseUrl, reportOwner, reportCreator, description, ownerEmail);
    }
}