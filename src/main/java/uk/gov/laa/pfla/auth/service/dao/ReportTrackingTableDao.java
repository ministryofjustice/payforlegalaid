package uk.gov.laa.pfla.auth.service.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import uk.gov.laa.pfla.auth.service.models.ReportTrackingTableModel;

import java.sql.*;

@Repository
@Slf4j
public class ReportTrackingTableDao {

    private final JdbcTemplate writeJdbcTemplate;
    private SimpleJdbcInsert insertActor;

    @Value("${spring.datasource.write.url}")
    String dbUrl;

    @Value("${spring.datasource.write.username}")
    String dbUser;

    @Value("${spring.datasource.write.password}")
    String dbPassword;


//    private int id;
//    private String reportName;
//    private String reportUrl; // The sharepoint URL where the report is stored, after being created
//    private LocalDateTime creationTime;
//    private int mappingId;
//    private String reportGeneratedBy;

    // Using the JDBCTemplate bean defined in  PflaApplication (the @SpringBootApplication / run class) which uses the
    // DB datasource/credentials with write permissions
    @Autowired
    public ReportTrackingTableDao(JdbcTemplate readOnlyJdbcTemplate, JdbcTemplate writeJdbcTemplate){
        this.writeJdbcTemplate = writeJdbcTemplate;

    }

    public void updateTrackingTable(ReportTrackingTableModel trackingModel) {

//        String sql = "INSERT INTO GPFD.REPORT_TRACKING (ID, REPORT_NAME, REPORT_URL, CREATION_TIME, MAPPING_ID, REPORT_GENERATED_BY) VALUES (?,?,?,?,?,?)";
//        Timestamp timestamp = Timestamp.valueOf(trackingModel.getCreationTime());
//
//        //Insert values into sql statement and update
//        writeJdbcTemplate.update(sql, trackingModel.getId(), trackingModel.getReportName(), trackingModel.getReportUrl(),
//                timestamp, trackingModel.getMappingId(), trackingModel.getReportGeneratedBy());

//        writeJdbcTemplate.update(sql);


//        String sql = "INSERT INTO GPFD.REPORT_TRACKING (ID, REPORT_NAME, REPORT_URL, CREATION_TIME, MAPPING_ID, REPORT_GENERATED_BY) VALUES (?,?,?,?,?,?)";
//
//        //Insert values into sql statement and update
//        writeJdbcTemplate.update(sql, 1, "Test name 1", "www.test-site.com",
//                trackingModel.getCreationTime() , 2, "Tony Soprano");



//        String sql = "INSERT INTO GPFD.REPORT_TRACKING (ID, REPORT_NAME, REPORT_URL, CREATION_TIME, MAPPING_ID, REPORT_GENERATED_BY) VALUES (1, 'test report name 2', 'www.sharepoint.com/place-where-we-will-create-report', null, 2, 'Liv Tyler')";

        //Insert values into sql statement and update
//        jdbcTemplate.update(sql);
//

//        this.jdbcTemplate.update(
//                "INSERT INTO GPFD.REPORT_TRACKING (ID, REPORT_NAME, REPORT_URL, CREATION_TIME, MAPPING_ID, REPORT_GENERATED_BY) VALUES (?,?,?,?,?,?)",
//                trackingModel.getId(), trackingModel.getReportName(), trackingModel.getReportUrl(),
//                null, trackingModel.getMappingId(), trackingModel.getReportGeneratedBy());






//        SimpleJdbcInsert simpleJdbcInsert =
//                new SimpleJdbcInsert(writeJdbcTemplate).withTableName("REPORT_TRACKING");
//
//        Map<String,Object> parameters = new HashMap<>();
//        parameters.put("ID",trackingModel.getId());
//        parameters.put("REPORT_NAME",trackingModel.getReportName());
//        parameters.put("REPORT_URL",trackingModel.getReportUrl());
//        parameters.put("CREATION_TIME",trackingModel.getCreationTime());
//        parameters.put("MAPPING_ID",trackingModel.getMappingId());
//        parameters.put("REPORT_GENERATED_BY",trackingModel.getReportGeneratedBy());
//
//        simpleJdbcInsert.execute(parameters);


        //Attempt with manual JDBC connection

        Timestamp timestamp = Timestamp.valueOf(trackingModel.getCreationTime());

        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {

            String sql = "INSERT INTO GPFD.REPORT_TRACKING (ID, REPORT_NAME, REPORT_URL, CREATION_TIME, MAPPING_ID, REPORT_GENERATED_BY) VALUES (?,?,?,?,?,?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, trackingModel.getId());
                preparedStatement.setString(2, trackingModel.getReportName());
                preparedStatement.setString(3, trackingModel.getReportUrl());
                preparedStatement.setTimestamp(4, timestamp);
                preparedStatement.setInt(5, trackingModel.getMappingId());
                preparedStatement.setString(6, trackingModel.getReportGeneratedBy());
                int rowsAffected = preparedStatement.executeUpdate();
                log.info("Rows affected: " + rowsAffected);


            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



}
