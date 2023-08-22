package uk.gov.laa.pfla.auth.service.builders;

import uk.gov.laa.pfla.auth.service.responses.ReportResponse;

import java.time.LocalDateTime;

public class ReportResponseBuilder {
    private int id;
    private String reportName;
    private String reportUrl;
    private LocalDateTime creationTime;

    public ReportResponseBuilder withId(int id) {
        this.id = id;
        return this;
    }

    public ReportResponseBuilder withReportName(String reportName) {
        this.reportName = reportName;
        return this;
    }

    public ReportResponseBuilder withReportUrl(String reportUrl) {
        this.reportUrl = reportUrl;
        return this;
    }

    public ReportResponseBuilder withCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
        return this;
    }

    public ReportResponse createReportResponse() {
        return new ReportResponse(id, reportName, reportUrl, creationTime);
    }
}