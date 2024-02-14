package uk.gov.laa.pfla.auth.service.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    private String reportUrl; // The sharepoint URL where the report is stored, after being created
    private LocalDateTime creationTime;
    private String reportDownloadUrl;


}
