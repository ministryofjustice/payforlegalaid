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
    private String excelReport;
    private String csvName;
    private int excelSheetNum;
    private String sqlQuery;
    private String baseUrl;
    private String reportOwner;
    private String reportCreator;
    private String description;
    private String ownerEmail;


    public ReportListResponse(int id, String reportName, String excelReport, String csvName, int excelSheetNum, String sqlQuery, String baseUrl, String reportOwner, String reportCreator, String description, String ownerEmail) {
        this.id = id;
        this.reportName = reportName;
        this.excelReport = excelReport;
        this.csvName = csvName;
        this.excelSheetNum = excelSheetNum;
        this.sqlQuery = sqlQuery;
        this.baseUrl = baseUrl;
        this.reportOwner = reportOwner;
        this.reportCreator = reportCreator;
        this.description = description;
        this.ownerEmail = ownerEmail;
    }

    public ReportListResponse() {

    }
}
