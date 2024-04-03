package uk.gov.laa.pfla.auth.service.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * A class containing meta-data about a single report. These objects will be collected together as part of a list inside a ReportListResponse.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportListEntry {

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
