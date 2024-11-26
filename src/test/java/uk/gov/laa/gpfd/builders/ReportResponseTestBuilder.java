package uk.gov.laa.gpfd.builders;

import uk.gov.laa.gpfd.model.GetReportById200Response;

import java.net.URI;

public class ReportResponseTestBuilder {

    public static final int DEFAULT_ID = 1;
    public static final String DEFAULT_REPORT_NAME = "Excel_Report_Name-CSV-NAME-sheetnumber";
    public static final String DEFAULT_DOWNLOAD_URL = "www.testurlnotanactualaddress.org";

    private int id = DEFAULT_ID;
    private String reportName = DEFAULT_REPORT_NAME;

    private String reportDownloadUrl = DEFAULT_DOWNLOAD_URL;

    public ReportResponseTestBuilder withId(int id) {
        this.id = id;
        return this;
    }

    public ReportResponseTestBuilder withReportName(String reportName) {
        this.reportName = reportName;
        return this;
    }

    public ReportResponseTestBuilder withReportDownloadUrl(String reportDownloadUrl) {
        this.reportDownloadUrl = reportDownloadUrl;
        return this;
    }

    public GetReportById200Response createReportResponse() {
        return new GetReportById200Response() {{
            id(id);
            reportName(reportName);
            reportDownloadUrl(URI.create(reportDownloadUrl));
        }};
    }
}