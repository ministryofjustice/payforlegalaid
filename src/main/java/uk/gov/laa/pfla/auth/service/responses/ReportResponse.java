package uk.gov.laa.pfla.auth.service.responses;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * A class defining a /report endpoint response object. This response consists of an id, name, url and datetime
 * of the report.
 */
@Data
@Builder
public class ReportResponse {

    private int id;
    private String reportName;
    private String reportUrl; // The sharepoint URL where the report is stored, after being created
    private LocalDateTime creationTime;


    public ReportResponse(int id, String reportName, String reportUrl, LocalDateTime creationTime) {
        this.id = id;
        this.reportName = reportName;
        this.reportUrl = reportUrl;
        this.creationTime = creationTime;

    }

    public ReportResponse() {
        //no args constructor needed for ModelMapper
    }
}
