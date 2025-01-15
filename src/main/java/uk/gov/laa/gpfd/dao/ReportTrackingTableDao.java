package uk.gov.laa.gpfd.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import uk.gov.laa.gpfd.model.ReportTrackingTable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ReportTrackingTableDao {
    private static final String INSERT_SQL = "INSERT INTO GPFD.REPORT_TRACKING (ID, REPORT_NAME, REPORT_URL, CREATION_TIME, MAPPING_ID, REPORT_GENERATED_BY) VALUES (?,?,?,?,?,?)";
    private static final String SELECT_SQL = "SELECT * from GPFD.REPORT_TRACKING";

    private final JdbcTemplate writeJdbcTemplate;

    public void updateTrackingTable(ReportTrackingTable trackingModel) {
        log.info("Updating tracking information");
        int numberOfRowsAffected = this.writeJdbcTemplate.update(INSERT_SQL, UUID.randomUUID().toString(), trackingModel.reportName(), trackingModel.reportUrl(),
                trackingModel.creationTime(), trackingModel.mappingId(), trackingModel.reportGeneratedBy());
        log.debug("Number of database rows affected by insert to report tracking table: " + numberOfRowsAffected);
    }

    public List<Map<String, Object>> list() {
        return writeJdbcTemplate.queryForList(SELECT_SQL);
    }

}
