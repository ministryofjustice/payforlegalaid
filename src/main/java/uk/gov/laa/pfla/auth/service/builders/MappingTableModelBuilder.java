package uk.gov.laa.pfla.auth.service.builders;

import uk.gov.laa.pfla.auth.service.models.MappingTableModel;

public class MappingTableModelBuilder {
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