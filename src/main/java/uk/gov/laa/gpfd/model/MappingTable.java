package uk.gov.laa.gpfd.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A class representing the data in the MOJFIN 'CSV - SQL Mapping' Table. A subset of this data will eventually be
 * returned to the user via the /reports endpoint, in the form of a ReportListResponse
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MappingTable {
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
