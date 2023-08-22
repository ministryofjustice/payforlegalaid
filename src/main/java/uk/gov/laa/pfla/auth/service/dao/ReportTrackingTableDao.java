package uk.gov.laa.pfla.auth.service.dao;

import org.springframework.stereotype.Repository;
import uk.gov.laa.pfla.auth.service.models.ReportTrackingTableModel;
import java.time.LocalDateTime;

@Repository
public class ReportTrackingTableDao {
    private int id;
    private String reportName;
    private String reportUrl; // The sharepoint URL where the report is stored, after being created
    private LocalDateTime creationTime;
    private int mappingId;
    private String reportGeneratedBy;


    public void updateTable(ReportTrackingTableModel reportTrackingTableModel) {

        //update database table

    }


}
