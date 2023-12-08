package uk.gov.laa.pfla.auth.service.models;

import lombok.Data;

import java.time.LocalDateTime;
//TODO - This class will be a bean, dont forget to annotate it with  @org.springframework.beans.factory.annotation.Autowired to make sonarlint happy
// Bean guide: https://www.baeldung.com/spring-bean

/**
 * A class representing the data in the MOJFIN 'Report Tracking' Table.
 */
@Data
public class ReportTrackingTableModel {

    private int id;
    private String reportName;
    private String reportUrl; // The sharepoint URL where the report is stored, after being created
    private LocalDateTime creationTime;
    private int mappingId;
    private String reportGeneratedBy;



    public ReportTrackingTableModel(int id, String reportName, String reportUrl, LocalDateTime creationTime, int mappingId, String reportGeneratedBy ) {
        this.id = id;
        this.reportName = reportName;
        this.reportUrl = reportUrl;
        this.creationTime = creationTime;
        this.mappingId = mappingId;
        this.reportGeneratedBy = reportGeneratedBy;


    }

    public ReportTrackingTableModel() {
        //no args constructor needed for ModelMapper
    }
}
