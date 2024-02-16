package uk.gov.laa.pfla.auth.service.builders;

import uk.gov.laa.pfla.auth.service.responses.ReportResponse;

import java.time.LocalDateTime;
import java.time.Month;

public class ReportResponseTestBuilder {

    public static final int DEFAULT_ID = 1;
    public static final String DEFAULT_REPORT_NAME = "Excel_Report_Name-CSV-NAME-sheetnumber";
    public static final String DEFAULT_URL = "www.sharepoint.com/folder-for-storing-created-reports";

    public static final String DEFAULT_DOWNLOAD_URL = "www.testurlnotanactualaddress.org";

    public static final LocalDateTime DEFAULT_TIME = LocalDateTime.of(2023,
            Month.AUGUST, 29, 19, 30, 40);

    private int id = DEFAULT_ID;
    private String reportName = DEFAULT_REPORT_NAME;
    private String reportUrl = DEFAULT_URL;
    private LocalDateTime creationTime = DEFAULT_TIME;

    private String reportDownloadUrl = DEFAULT_DOWNLOAD_URL;


    public ReportResponseTestBuilder withId(int id) {
        this.id = id;
        return this;
    }

    public ReportResponseTestBuilder withReportName(String reportName) {
        this.reportName = reportName;
        return this;
    }

    //These fields will be useful when caching/content management is brought into the app

//    public ReportResponseTestBuilder withReportSharepointUrl(String reportUrl) {
//        this.reportUrl = reportUrl;
//        return this;
//    }
//
//    public ReportResponseTestBuilder withCreationTime(LocalDateTime creationTime) {
//        this.creationTime = creationTime;
//        return this;
//    }

    public ReportResponseTestBuilder withReportDownloadUrl(String reportDownloadUrl) {
        this.reportDownloadUrl = reportDownloadUrl;
        return this;
    }

    public ReportResponse createReportResponse() {
        return new ReportResponse(id, reportName, reportDownloadUrl );
    }
}