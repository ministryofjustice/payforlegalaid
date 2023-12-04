package uk.gov.laa.pfla.auth.service.builders;

import uk.gov.laa.pfla.auth.service.models.MappingTableModel;

import java.sql.Date;

public class MappingTableModelBuilder {
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

    public MappingTableModelBuilder withId(int id) {
        this.id = id;
        return this;
    }

    public MappingTableModelBuilder withReportName(String reportName) {
        this.reportName = reportName;
        return this;
    }

    public MappingTableModelBuilder withExcelReport(String excelReport) {
        this.excelReport = excelReport;
        return this;
    }

    public MappingTableModelBuilder withCsvName(String csvName) {
        this.csvName = csvName;
        return this;
    }

    public MappingTableModelBuilder withExcelSheetNumber(int excelSheetNum) {
        this.excelSheetNum = excelSheetNum;
        return this;
    }

    public MappingTableModelBuilder withSqlQuery(String sqlQuery) {
        this.sqlQuery = sqlQuery;
        return this;
    }

    public MappingTableModelBuilder withBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
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

    public MappingTableModelBuilder withOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
        return this;
    }



    public MappingTableModel build() {
        return new MappingTableModel(id, reportName, excelReport, csvName, excelSheetNum, sqlQuery, baseUrl, reportOwner, reportCreator, description, ownerEmail);
    }
}