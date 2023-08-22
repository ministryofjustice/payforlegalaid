package uk.gov.laa.pfla.auth.service.builders;

import uk.gov.laa.pfla.auth.service.models.MappingTableModel;

public class MappingTableModelBuilder {
    private int id;
    private String reportName;
    private String reportPeriod;
    private String reportOwner;
    private String reportCreator;
    private String reportDescription;
    private String baseUrl;
    private String sql;

    public MappingTableModelBuilder withId(int id) {
        this.id = id;
        return this;
    }

    public MappingTableModelBuilder withReportName(String reportName) {
        this.reportName = reportName;
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
        this.reportDescription = description;
        return this;
    }

    public MappingTableModelBuilder withBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    public MappingTableModelBuilder withSql(String sql) {
        this.sql = sql;
        return this;
    }



    public MappingTableModel createMappingTableModel() {
        return new MappingTableModel(id, reportName, reportPeriod, reportOwner, reportCreator, reportDescription, baseUrl, sql);
    }
}