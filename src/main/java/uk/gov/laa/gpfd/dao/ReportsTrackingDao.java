package uk.gov.laa.gpfd.dao;

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
      log.debug("vvvv Saving tracking information for report ID:{}, creator: {}", reportsTracking.getReportId(), reportsTracking.getReportCreator());
      int numberOfRowsAffected = this.writeJdbcTemplate.update(INSERT_SQL, reportsTracking.getId().toString(), reportsTracking.getName(),
          reportsTracking.getReportId().toString(), reportsTracking.getCreationDate(), reportsTracking.getReportCreator(),
          reportsTracking.getReportOwner(), reportsTracking.getReportOutputType(), reportsTracking.getTemplateUrl(), reportsTracking.getReportUrl());
      log.debug("vvvv Number of database rows affected by insert to report tracking table: " + numberOfRowsAffected);
    } catch (Exception e) {
//      log.error("vvvv Error saving tracking information for report ID:{}, creator: {}"
//          , reportsTracking.getReportId()
//          , reportsTracking.getReportCreator()
//          , e);

      log.error("vvvv Error saving tracking information for tracking ID:{}, name: {}, rep id: {}, cre dt: {}, creator:{}, owner: {}, output: {}, tepl: {}, repUrl: {}"
          ,reportsTracking.getId().toString(), reportsTracking.getName(),
          (reportsTracking.getReportId() == null? "null":reportsTracking.getReportId().toString()), reportsTracking.getCreationDate(), reportsTracking.getReportCreator(),
          reportsTracking.getReportOwner(), reportsTracking.getReportOutputType(), reportsTracking.getTemplateUrl(), reportsTracking.getReportUrl()
          , e);

    }
  }

}
