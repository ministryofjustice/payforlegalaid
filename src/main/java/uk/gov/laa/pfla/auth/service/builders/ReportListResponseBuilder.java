package uk.gov.laa.pfla.auth.service.builders;

import uk.gov.laa.pfla.auth.service.responses.ReportListResponse;

public class ReportListResponseBuilder {

    private int id;
    private String reportName;
    private String reportPeriod;
    private String reportOwner;
    private String reportCreator;
    private String reportDescription;
    private String baseUrl;

    public ReportListResponseBuilder withId(int id) {
        this.id = id;
        return this;
    }

    public ReportListResponseBuilder withReportName(String reportName) {
        this.reportName = reportName;
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
        this.reportDescription = reportDescription;
        return this;
    }

    public ReportListResponseBuilder withBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    public ReportListResponse createReportListResponse() {
        return new ReportListResponse(id, reportName, reportPeriod, reportOwner, reportCreator, reportDescription, baseUrl);
    }
}