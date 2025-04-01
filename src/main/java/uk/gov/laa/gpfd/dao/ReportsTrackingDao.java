package uk.gov.laa.gpfd.dao;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import uk.gov.laa.gpfd.model.ReportsTracking;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ReportsTrackingDao {
  private static final String INSERT_SQL = "INSERT INTO GPFD.REPORTS_TRACKING"
      + " (ID, NAME, REPORT_ID, CREATION_DATE, REPORT_CREATOR, REPORT_OWNER, REPORT_OUTPUT_TYPE, TEMPLATE_URL, REPORT_URL)"
      + " VALUES (?,?,?,?,?,?,?,?,?)";

  private final JdbcTemplate writeJdbcTemplate;

  public void saveReportsTracking(ReportsTracking reportsTracking) {
    try {
      log.debug("Saving tracking information for report ID:{}", reportsTracking.getReportId());
      int numberOfRowsAffected = this.writeJdbcTemplate.update(INSERT_SQL, UUID.randomUUID().toString(), reportsTracking.getName(),
          reportsTracking.getReportId(), reportsTracking.getCreationDate(), reportsTracking.getReportCreator(),
          reportsTracking.getReportOwner(), reportsTracking.getReportOutputType(), reportsTracking.getTemplateUrl(), reportsTracking.getReportUrl());
      log.debug("Number of database rows affected by insert to report tracking table: " + numberOfRowsAffected);
    } catch (Exception e) {
      log.error("Error saving tracking information for report ID:{}, creator: {}"
          , reportsTracking.getReportId()
          , reportsTracking.getReportCreator()
          , e);
    }
  }

}
