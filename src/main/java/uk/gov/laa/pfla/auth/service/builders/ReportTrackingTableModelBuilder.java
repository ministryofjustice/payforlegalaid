package uk.gov.laa.pfla.auth.service.builders;

import uk.gov.laa.pfla.auth.service.models.ReportTrackingTableModel;

import java.time.LocalDateTime;

public class ReportTrackingTableModelBuilder {
    private int id;
    private String reportName;
    private String reportUrl;
    private LocalDateTime creationTime;
    private int mappingId;
    private String reportGeneratedBy;

    public ReportTrackingTableModelBuilder withId(int id) {
        this.id = id;
        return this;
    }

    public ReportTrackingTableModelBuilder withReportName(String reportName) {
        this.reportName = reportName;
        return this;
    }

    public ReportTrackingTableModelBuilder withReportUrl(String reportUrl) {
        this.reportUrl = reportUrl;
        return this;
    }

    public ReportTrackingTableModelBuilder withCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
        return this;
    }

    public ReportTrackingTableModelBuilder withMappingId(int mappingId) {
        this.mappingId = mappingId;
        return this;
    }

    public ReportTrackingTableModelBuilder withReportGeneratedBy(String reportGeneratedBy) {
        this.reportGeneratedBy = reportGeneratedBy;
        return this;
    }

    public ReportTrackingTableModel createReportTrackingTableModel() {
        return new ReportTrackingTableModel(id, reportName, reportUrl, creationTime, mappingId, reportGeneratedBy);
    }
}