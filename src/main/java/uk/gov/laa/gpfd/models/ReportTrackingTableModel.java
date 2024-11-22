package uk.gov.laa.gpfd.models;

import lombok.*;

import java.sql.Timestamp;
//TODO - This class will be a bean, dont forget to annotate it with  @org.springframework.beans.factory.annotation.Autowired to make sonarlint happy
// Bean guide: https://www.baeldung.com/spring-bean

/**
 * A class representing the data in the MOJFIN 'Report Tracking' Table.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor //Needed by the modelmapper library
@ToString
public class ReportTrackingTableModel {

    //An id field/column is not necessary in this model, since the id primary key is auto-incremented in the tracking table, using an oracle DB sequence
    private int id;
    private String reportName;
    private String reportUrl; // The sharepoint URL where the report is stored, after being created
    private Timestamp creationTime;
    private int mappingId;
    private String reportGeneratedBy;


}
