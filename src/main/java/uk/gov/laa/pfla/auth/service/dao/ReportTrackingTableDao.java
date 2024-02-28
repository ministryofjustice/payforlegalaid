package uk.gov.laa.pfla.auth.service.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import uk.gov.laa.pfla.auth.service.models.ReportTrackingTableModel;

@Repository
public class ReportTrackingTableDao {

    private final JdbcTemplate jdbcTemplate;
//    private int id;
//    private String reportName;
//    private String reportUrl; // The sharepoint URL where the report is stored, after being created
//    private LocalDateTime creationTime;
//    private int mappingId;
//    private String reportGeneratedBy;

    //Note: Spring autowires @Repository constructors automatically
    public ReportTrackingTableDao(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;

    }

    public void updateTrackingTable(ReportTrackingTableModel trackingModel) {
//
//        String sql = "INSERT INTO REPORT_TRACKING (ID, REPORT_NAME, REPORT_URL, CREATION_TIME, MAPPING_ID, REPORT_GENERATED_BY) VALUES (?,?,?,?,?,?)";
//
//        //Insert values into sql statement and update
//        jdbcTemplate.update(sql, trackingModel.getId(), trackingModel.getReportName(), trackingModel.getReportUrl(),
//                trackingModel.getCreationTime(), trackingModel.getMappingId(), trackingModel.getReportGeneratedBy());


        String sql = "INSERT INTO REPORT_TRACKING (ID, REPORT_NAME, REPORT_URL, CREATION_TIME, MAPPING_ID, REPORT_GENERATED_BY) VALUES (1,'test report name','www.sharepoint.com/place-where-we-will-create-report',null, 2, 'Barry White')";

        //Insert values into sql statement and update
        jdbcTemplate.update(sql);


    }


}
