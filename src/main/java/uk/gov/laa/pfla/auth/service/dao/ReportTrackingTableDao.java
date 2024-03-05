package uk.gov.laa.pfla.auth.service.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import uk.gov.laa.pfla.auth.service.models.ReportTrackingTableModel;

import java.sql.*;

@Repository
@Slf4j
public class ReportTrackingTableDao {

    private final JdbcTemplate writeJdbcTemplate;


    // Using the JDBCTemplate bean defined in  PflaApplication (the @SpringBootApplication / run class) which uses the
    // DB datasource/credentials with write permissions
    @Autowired
    public ReportTrackingTableDao(JdbcTemplate writeJdbcTemplate){
        this.writeJdbcTemplate = writeJdbcTemplate;

    }

    public void updateTrackingTable(ReportTrackingTableModel trackingModel) {

        String sql = "INSERT INTO GPFD.REPORT_TRACKING (ID, REPORT_NAME, REPORT_URL, CREATION_TIME, MAPPING_ID, REPORT_GENERATED_BY) VALUES (GPFD_TRAKING_TABLE_SEQUENCE.NEXTVAL,?,?,?,?,?)";
        Timestamp timestamp = Timestamp.valueOf(trackingModel.getCreationTime());

        //Insert values into sql statement and update
        int numberOfRowsAffected = writeJdbcTemplate.update(sql, trackingModel.getReportName(), trackingModel.getReportUrl(),
                timestamp, trackingModel.getMappingId(), trackingModel.getReportGeneratedBy());

        log.info("Number of database rows affected by insert to report tracking table: " + numberOfRowsAffected);

    }



}
