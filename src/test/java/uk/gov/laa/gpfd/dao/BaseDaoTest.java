package uk.gov.laa.gpfd.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import uk.gov.laa.gpfd.utils.FileUtils;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseDaoTest {
  @Autowired
  protected JdbcTemplate writeJdbcTemplate;

  @BeforeEach
  void setup() {
    writeJdbcTemplate.execute(FileUtils.readResourceToString("gpfd_schema.sql"));
    writeJdbcTemplate.execute(FileUtils.readResourceToString("gpfd_data.sql"));
    writeJdbcTemplate.execute(FileUtils.readResourceToString("gpfd_reports_schema.sql"));
    writeJdbcTemplate.execute(FileUtils.readResourceToString("gpfd_reports_data.sql"));
  }

  @AfterEach
  void resetDatabase() {
    writeJdbcTemplate.update("DROP SEQUENCE IF EXISTS GPFD_TRACKING_TABLE_SEQUENCE");
    writeJdbcTemplate.update("DROP TABLE IF EXISTS GPFD.CSV_TO_SQL_MAPPING_TABLE");
    writeJdbcTemplate.update("DROP TABLE IF EXISTS GPFD.REPORT_TRACKING");
    writeJdbcTemplate.update("DROP TABLE IF EXISTS GPFD.REPORTS_TRACKING");
    writeJdbcTemplate.update("DROP TABLE IF EXISTS GPFD.FIELD_ATTRIBUTES");
    writeJdbcTemplate.update("DROP TABLE IF EXISTS GPFD.REPORT_QUERIES");
    writeJdbcTemplate.update("DROP TABLE IF EXISTS GPFD.REPORT_GROUPS");
    writeJdbcTemplate.update("DROP TABLE IF EXISTS GPFD.REPORTS");
    writeJdbcTemplate.update("DROP TABLE IF EXISTS GPFD.REPORT_OUTPUT_TYPES");
  }

  public void clearReportsTable() {
    writeJdbcTemplate.update("DELETE FROM GPFD.REPORTS_TRACKING");
    writeJdbcTemplate.update("DELETE FROM GPFD.REPORTS");
  }

}