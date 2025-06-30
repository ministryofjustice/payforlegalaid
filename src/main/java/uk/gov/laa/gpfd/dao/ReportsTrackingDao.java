package uk.gov.laa.gpfd.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Service;
import uk.gov.laa.gpfd.model.ReportsTracking;

@Slf4j
@Service
public record ReportsTrackingDao(JdbcOperations writeJdbcTemplate) {
  private static final String INSERT_SQL = """
        INSERT
            INTO GPFD.REPORTS_TRACKING
            (ID, NAME, REPORT_ID, CREATION_DATE, REPORT_CREATOR, REPORT_OWNER, REPORT_OUTPUT_TYPE, TEMPLATE_URL, REPORT_URL)
        VALUES
            (?,?,?,?,?,?,?,?,?)
  """;

  public void saveReportsTracking(ReportsTracking reportsTracking) {
      log.debug("Saving tracking information for report ID:{}, creator: {}", reportsTracking.getReportId(), reportsTracking.getReportCreator());
      int numberOfRowsAffected = this.writeJdbcTemplate.update(INSERT_SQL, reportsTracking.getIdAsString(), reportsTracking.getReportName(),
          reportsTracking.getReportId().toString(), reportsTracking.getCreationDate(), reportsTracking.getReportCreator(),
          reportsTracking.getReportOwner(), reportsTracking.getReportOutputType(), reportsTracking.getTemplateUrl(), reportsTracking.getReportUrl());
      log.debug("Number of database rows affected by insert to report tracking table: " + numberOfRowsAffected);
  }

}
