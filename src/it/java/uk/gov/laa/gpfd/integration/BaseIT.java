package uk.gov.laa.gpfd.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import uk.gov.laa.gpfd.utils.FileUtils;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseIT {

  @Autowired
  protected JdbcTemplate writeJdbcTemplate;

  @BeforeAll
  void setupDatabase() {
    String gpfdSqlSchema = FileUtils.readResourceToString("gpfd_schema.sql");
    String gpfdSqlData = FileUtils.readResourceToString("gpfd_data.sql");
    writeJdbcTemplate.execute(gpfdSqlSchema);
    writeJdbcTemplate.execute(gpfdSqlData);
    String gpfdSqlReportsSchema = FileUtils.readResourceToString("gpfd_reports_schema.sql");
    String gpfdSqlReportsData = FileUtils.readResourceToString("gpfd_reports_data.sql");
    writeJdbcTemplate.execute(gpfdSqlReportsSchema);
    writeJdbcTemplate.execute(gpfdSqlReportsData);

    String anyReportSqlSchema = FileUtils.readResourceToString("any_report_schema.sql");
    String anyReportSqlData = FileUtils.readResourceToString("any_report_data.sql");
    writeJdbcTemplate.execute(anyReportSqlSchema);
    writeJdbcTemplate.execute(anyReportSqlData);
    System.out.println("vvvv End of BeforeAll");
  }

  @AfterAll
  void resetDatabase() {
    System.out.println("vvvv Running AfterAll");
    writeJdbcTemplate.execute("DROP TABLE IF EXISTS GPFD.CSV_TO_SQL_MAPPING_TABLE");
    writeJdbcTemplate.execute("DROP TABLE IF EXISTS GPFD.REPORT_TRACKING");
    writeJdbcTemplate.execute("DROP TABLE IF EXISTS GPFD.REPORTS_TRACKING");
    writeJdbcTemplate.execute("DROP TABLE IF EXISTS GPFD.FIELD_ATTRIBUTES");
    writeJdbcTemplate.execute("DROP TABLE IF EXISTS GPFD.REPORT_QUERIES");
    writeJdbcTemplate.execute("DROP TABLE IF EXISTS GPFD.REPORT_GROUPS");
    writeJdbcTemplate.execute("DROP TABLE IF EXISTS GPFD.REPORTS");
    writeJdbcTemplate.execute("DROP TABLE IF EXISTS GPFD.REPORT_OUTPUT_TYPES");
    System.out.println("vvvv End of AfterAll");
  }
}