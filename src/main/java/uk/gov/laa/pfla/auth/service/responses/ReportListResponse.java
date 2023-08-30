package uk.gov.laa.pfla.auth.service.responses;

import lombok.Data;


/**
 * A class defining a /reports endpoint response object. This response consists of a list of data about a single report.
 * Multiple response objects are sent to the user, forming a list of reports in JSON format
 */
@Data
public class ReportListResponse {

    private int id;
    private String reportName;
    private String reportPeriod;
    private String reportOwner;
    private String reportCreator;
    private String description;
    private String baseUrl;


    public ReportListResponse(int id, String reportName, String reportPeriod, String reportOwner, String reportCreator, String reportDescription, String baseUrl) {
        this.id = id;
        this.reportName = reportName;
        this.reportPeriod = reportPeriod;
        this.reportOwner = reportOwner;
        this.reportCreator = reportCreator;
        this.description = reportDescription;
        this.baseUrl = baseUrl;

    }


    public ReportListResponse() {

    }
}