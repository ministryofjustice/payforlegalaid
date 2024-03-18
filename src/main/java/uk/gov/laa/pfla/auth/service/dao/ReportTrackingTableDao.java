package uk.gov.laa.pfla.auth.service.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import uk.gov.laa.pfla.auth.service.models.ReportTrackingTableModel;
import org.springframework.jdbc.core.RowMapper;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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

        JdbcTemplate localJdbcTemplate = this.writeJdbcTemplate;

        String sql = "INSERT INTO GPFD.REPORT_TRACKING (ID, REPORT_NAME, REPORT_URL, CREATION_TIME, MAPPING_ID, REPORT_GENERATED_BY) VALUES (GPFD_TRACKING_TABLE_SEQUENCE.NEXTVAL,?,?,?,?,?)";
        Timestamp timestamp = Timestamp.valueOf(trackingModel.getCreationTime());

        //Insert values into sql statement and update
        int numberOfRowsAffected = localJdbcTemplate.update(sql, trackingModel.getReportName(), trackingModel.getReportUrl(),
                timestamp, trackingModel.getMappingId(), trackingModel.getReportGeneratedBy());

        log.info("JDBC update arguments: " + sql + "  , " + trackingModel.getReportName() + "  , " + trackingModel.getReportUrl() + "  , " +
                timestamp + "  , " +  trackingModel.getMappingId() + "  , " + trackingModel.getReportGeneratedBy());

        log.info("Number of database rows affected by insert to report tracking table: " + numberOfRowsAffected);

    }


    public List<Map<String, Object>> list() {


        String sql = "SELECT * from REPORT_TRACKING";
        return writeJdbcTemplate.queryForList(sql);
    }



}
