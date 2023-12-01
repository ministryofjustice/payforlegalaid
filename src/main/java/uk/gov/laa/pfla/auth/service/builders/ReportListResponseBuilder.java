package uk.gov.laa.pfla.auth.service.builders;

import uk.gov.laa.pfla.auth.service.responses.ReportListResponse;

public class ReportListResponseBuilder {


    private int id;
    private String reportName;
    private String sqlString;
    private String baseUrl;
    private String reportPeriod;
    private String reportOwner;
    private String reportCreator;
    private String description;
    private int excelSheetNum;
    private String csvName;

    public ReportListResponseBuilder(int id, String reportName, String sqlString, String baseUrl, String reportPeriod, String reportOwner, String reportCreator, String description, int excelSheetNum, String csvName) {
        this.id = id;
        this.reportName = reportName;
        this.sqlString = sqlString;
        this.baseUrl = baseUrl;
        this.reportPeriod = reportPeriod;
        this.reportOwner = reportOwner;
        this.reportCreator = reportCreator;
        this.description = description;
        this.excelSheetNum = excelSheetNum;
        this.csvName = csvName;
    }

    public ReportListResponseBuilder withId(int id) {
        this.id = id;
        return this;
    }

    public ReportListResponseBuilder withReportName(String reportName) {
        this.reportName = reportName;
        return this;
    }

    public ReportListResponseBuilder withSqlString(String sqlString) {
        this.sqlString = sqlString;
        return this;
    }

    public ReportListResponseBuilder withReportPeriod(String reportPeriod) {
        this.reportPeriod = reportPeriod;
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

    public ReportListResponseBuilder withReportDescription(String reportDescription) {
        this.description = reportDescription;
        return this;
    }

    public ReportListResponseBuilder withExcelSheetNumber(int excelSheetNum) {
        this.excelSheetNum = excelSheetNum;
        return this;
    }

    public ReportListResponseBuilder withBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    public ReportListResponseBuilder withCsvName(String csvName) {
        this.csvName = csvName;
        return this;
    }


    public ReportListResponse createReportListResponse() {
        return new ReportListResponse(id, reportName, sqlString, baseUrl, reportPeriod,  reportOwner, reportCreator, description, excelSheetNum, csvName);
    }
}