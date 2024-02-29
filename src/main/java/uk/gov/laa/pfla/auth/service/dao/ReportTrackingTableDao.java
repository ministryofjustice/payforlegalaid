package uk.gov.laa.pfla.auth.service.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import uk.gov.laa.pfla.auth.service.models.ReportTrackingTableModel;

import java.util.HashMap;
import java.util.Map;

@Repository
public class ReportTrackingTableDao {

    private final JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert insertActor;

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

//        String sql = "INSERT INTO GPFD.REPORT_TRACKING (ID, REPORT_NAME, REPORT_URL, CREATION_TIME, MAPPING_ID, REPORT_GENERATED_BY) VALUES (?,?,?,?,?,?)";
//
//        //Insert values into sql statement and update
//        jdbcTemplate.update(sql, trackingModel.getId(), trackingModel.getReportName(), trackingModel.getReportUrl(),
//                trackingModel.getCreationTime(), trackingModel.getMappingId(), trackingModel.getReportGeneratedBy());


//        String sql = "INSERT INTO GPFD.REPORT_TRACKING (ID, REPORT_NAME, REPORT_URL, CREATION_TIME, MAPPING_ID, REPORT_GENERATED_BY) VALUES (?,?,?,?,?,?)";
//
//        //Insert values into sql statement and update
//        jdbcTemplate.update(sql, 1, "Test name 1", "www.test-site.com",
//                null , 2, "Tony Soprano");



//        String sql = "INSERT INTO GPFD.REPORT_TRACKING (ID, REPORT_NAME, REPORT_URL, CREATION_TIME, MAPPING_ID, REPORT_GENERATED_BY) VALUES (1, 'test report name 2', 'www.sharepoint.com/place-where-we-will-create-report', null, 2, 'Liv Tyler')";

        //Insert values into sql statement and update
//        jdbcTemplate.update(sql);
//

//        this.jdbcTemplate.update(
//                "INSERT INTO GPFD.REPORT_TRACKING (ID, REPORT_NAME, REPORT_URL, CREATION_TIME, MAPPING_ID, REPORT_GENERATED_BY) VALUES (?,?,?,?,?,?)",
//                trackingModel.getId(), trackingModel.getReportName(), trackingModel.getReportUrl(),
//                null, trackingModel.getMappingId(), trackingModel.getReportGeneratedBy());






        SimpleJdbcInsert simpleJdbcInsert =
                new SimpleJdbcInsert(jdbcTemplate).withTableName("REPORT_TRACKING");

        Map<String,Object> parameters = new HashMap<>();
        parameters.put("ID",trackingModel.getId());
        parameters.put("REPORT_NAME",trackingModel.getReportName());
        parameters.put("REPORT_URL",trackingModel.getReportUrl());
        parameters.put("CREATION_TIME",trackingModel.getCreationTime());
        parameters.put("MAPPING_ID",trackingModel.getMappingId());
        parameters.put("REPORT_GENERATED_BY",trackingModel.getReportGeneratedBy());

        simpleJdbcInsert.execute(parameters);

    }


}
