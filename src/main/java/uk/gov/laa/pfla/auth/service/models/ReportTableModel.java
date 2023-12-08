package uk.gov.laa.pfla.auth.service.models;

import lombok.Data;

import java.time.LocalDateTime;

//TODO - This class will be a bean, dont forget to annotate it with  @org.springframework.beans.factory.annotation.Autowired to make sonarlint happy
// Bean guide: https://www.baeldung.com/spring-bean

/**
 * A class representing the data in the MOJFIN reports Table. A subset of this data will eventually be
 * returned to the user via the /report endpoint, in the form of a ReportListResponse
 */
@Data
public class ReportTableModel {

    private int id;
    private String reportName;
    private String reportUrl; // The sharepoint URL where the report is stored, after being created
    private LocalDateTime creationTime;




    public ReportTableModel(int id, String reportName, String reportUrl, LocalDateTime creationTime) {
        this.id = id;
        this.reportName = reportName;
        this.reportUrl = reportUrl;
        this.creationTime = creationTime;

    }

    public ReportTableModel() {
        //no args constructor needed for ModelMapper
    }




}
