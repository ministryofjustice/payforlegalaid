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
    private String sqlString;
    private String baseUrl;
    private String reportPeriod;
    private String reportOwner;
    private String reportCreator;
    private String description;
    private int excelSheetNum;
    private String csvName;

    public ReportListResponse(int id, String reportName, String sqlString, String baseUrl, String reportPeriod, String reportOwner, String reportCreator, String description, int excelSheetNum, String csvName) {
        this.id = id;
        this.reportName = reportName;
        this.sqlString = sqlString;
        this.baseUrl = baseUrl;
        this.reportPeriod = reportPeriod;
        this.reportOwner = reportOwner;
        this.reportCreator = reportCreator;
        this.description = description;
        this.excelSheetNum = excelSheetNum;
        this.csvName = csvName;
    }


    public ReportListResponse() {

    }
}
