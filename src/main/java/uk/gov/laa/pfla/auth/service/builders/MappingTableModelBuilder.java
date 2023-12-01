package uk.gov.laa.pfla.auth.service.builders;

import uk.gov.laa.pfla.auth.service.models.MappingTableModel;

import java.sql.Date;

public class MappingTableModelBuilder {
    private int id;
    private String reportName;
    private String sqlString;
    private String baseUrl;
    private String reportPeriod;
    private String reportOwner;
    private String reportCreator;
    private String description;

    private Date reportPeriodFrom;

    private Date reportPeriodTo;

    private String excelReport;

    private int excelSheetNum;
    private String csvName;

    public MappingTableModelBuilder withId(int id) {
        this.id = id;
        return this;
    }

    public MappingTableModelBuilder withReportName(String reportName) {
        this.reportName = reportName;
        return this;
    }

    public MappingTableModelBuilder withSqlString(String sqlString) {
        this.sqlString = sqlString;
        return this;
    }
    public MappingTableModelBuilder withReportPeriod(String reportPeriod) {
        this.reportPeriod = reportPeriod;
        return this;
    }

    public MappingTableModelBuilder withReportOwner(String reportOwner) {
        this.reportOwner = reportOwner;
        return this;
    }

    public MappingTableModelBuilder withReportCreator(String reportCreator) {
        this.reportCreator = reportCreator;
        return this;
    }

    public MappingTableModelBuilder withReportDescription(String description) {
        this.description = description;
        return this;
    }


    public MappingTableModelBuilder withReportPeriodFrom(Date reportPeriodFrom) {
        this.reportPeriodFrom = reportPeriodFrom;
        return this;
    }

    public MappingTableModelBuilder withReportPeriodTo(Date reportPeriodTo) {
        this.reportPeriodTo = reportPeriodTo;
        return this;
    }

    public MappingTableModelBuilder withExcelReport(String excelReport) {
        this.excelReport = excelReport;
        return this;
    }
    public MappingTableModelBuilder withExcelSheetNumber(int excelSheetNum) {
        this.excelSheetNum = excelSheetNum;
        return this;
    }

    public MappingTableModelBuilder withBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    public MappingTableModelBuilder withCsvName(String csvName) {
        this.csvName = csvName;
        return this;
    }



    public MappingTableModel build() {
        return new MappingTableModel(id, reportName, sqlString, baseUrl, reportPeriod, reportOwner, reportCreator, description, excelSheetNum, csvName );
    }
}