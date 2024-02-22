package uk.gov.laa.pfla.auth.service.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * A class defining a /reports endpoint response object. This response consists of a list of data about a single report.
 * Multiple response objects are sent to the user, forming a list of reports in JSON format
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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


}
