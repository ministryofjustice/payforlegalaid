package uk.gov.laa.pfla.auth.service.builders;

import uk.gov.laa.pfla.auth.service.responses.ReportListResponse;

public class ReportListResponseTestBuilder {

    public static final int DEFAULT_ID = 1;
    public static final String DEFAULT_REPORT_NAME = "Excel_Report_Name-CSV-NAME-sheetnumber";
    public static final String DEFAULT_REPORT_PERIOD = "01/08/2023 - 01/09/2023";
    public static final String DEFAULT_REPORT_OWNER = "Chancey Mctavish";
    public static final String DEFAULT_REPORT_CREATOR = "Barry Gibb";
    public static final String DEFAULT_REPORT_DESCRIPTION= "List all unpaid AP invoices and all outstanding AR debts at the end of the previous month. Detailed data, one row per invoice";
    public static final String DEFAULT_BASE_URL = "www.sharepoint.com/the-folder-we're-using";


    private int id = DEFAULT_ID;
    private String reportName = DEFAULT_REPORT_NAME;
    private String reportPeriod = DEFAULT_REPORT_PERIOD;
    private String reportOwner = DEFAULT_REPORT_OWNER;
    private String reportCreator = DEFAULT_REPORT_CREATOR;
    private String reportDescription = DEFAULT_REPORT_DESCRIPTION;
    private String baseUrl = DEFAULT_BASE_URL;


    public ReportListResponseTestBuilder withId(int id) {
        this.id = id;
        return this;
    }

    public ReportListResponseTestBuilder withReportName(String reportName) {
        this.reportName = reportName;
        return this;
    }

    public ReportListResponseTestBuilder withReportPeriod(String reportPeriod) {
        this.reportPeriod = reportPeriod;
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

    public ReportListResponseTestBuilder withReportDescription(String reportDescription) {
        this.reportDescription = reportDescription;
        return this;
    }

    public ReportListResponseTestBuilder withBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    public ReportListResponse createReportListResponse() {
        return new ReportListResponse(id, reportName, reportPeriod, reportOwner, reportCreator, reportDescription, baseUrl);
    }
}