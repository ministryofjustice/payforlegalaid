package uk.gov.laa.pfla.auth.service.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import uk.gov.laa.pfla.auth.service.models.ReportTrackingTableModel;

import java.util.List;
import java.util.Map;

@Repository
@Slf4j
@RequiredArgsConstructor //This creates a constructor for all final fields, @Repository will autowire it
public class ReportTrackingTableDao {

    // Using the JDBCTemplate bean defined in  PflaApplication (the @SpringBootApplication / run class) which uses the
    // DB datasource/credentials with write permissions
    private final JdbcTemplate writeJdbcTemplate;


    public void updateTrackingTable(ReportTrackingTableModel trackingModel) {


        JdbcTemplate localJdbcTemplate = this.writeJdbcTemplate;

        String sql = "INSERT INTO GPFD.REPORT_TRACKING (ID, REPORT_NAME, REPORT_URL, CREATION_TIME, MAPPING_ID, REPORT_GENERATED_BY) VALUES (GPFD_TRACKING_TABLE_SEQUENCE.NEXTVAL,?,?,?,?,?)";

        //Insert values into sql statement and update
        log.info("Updating tracking information");
        int numberOfRowsAffected = localJdbcTemplate.update(sql, trackingModel.getReportName(), trackingModel.getReportUrl(),
                trackingModel.getCreationTime(), trackingModel.getMappingId(), trackingModel.getReportGeneratedBy());


        log.debug("Number of database rows affected by insert to report tracking table: " + numberOfRowsAffected);

    }


    public List<Map<String, Object>> list() {

        String sql = "SELECT * from GPFD.REPORT_TRACKING";
        return writeJdbcTemplate.queryForList(sql);
    }


}
