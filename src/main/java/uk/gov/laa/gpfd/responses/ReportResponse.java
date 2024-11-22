package uk.gov.laa.gpfd.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * A class defining a /report endpoint response object. This response consists of an id, name, url and datetime
 * of the report.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportResponse {

    private int id;
    private String reportName;
//    private String reportSharepointUrl; // The sharepoint URL where the report is stored, after being created
//    private LocalDateTime creationTime;
    private String reportDownloadUrl;


}
